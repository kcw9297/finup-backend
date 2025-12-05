package app.finup.layer.domain.level.service;

import app.finup.layer.domain.level.dto.LevelDto;
import app.finup.layer.domain.level.entity.Level;
import app.finup.layer.domain.level.entity.UserLevelProgress;
import app.finup.layer.domain.level.repository.LevelRepository;
import app.finup.layer.domain.level.repository.UserLevelProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 개념 학습 단계 Service 구현 클래스
 * - JPA 기반 CRUD 처리
 * - 회원별 진도 매핑 처리
 * @author sjs
 * @since 2025-12-05
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LevelServiceImpl implements LevelService {

    private final LevelRepository levelRepository;
    private final UserLevelProgressRepository progressRepository;

    /**
     * 단계 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<LevelDto.Row> getList(Long memberId) {

        // 전체 단계 순서대로 조회
        List<Level> levels = levelRepository.findAllByOrderByOrderNumberAsc();

        // 회원별 진행도 맵으로 변환
        Map<Long, UserLevelProgress> progressMap = new HashMap<>();

        if (memberId != null) {
            List<UserLevelProgress> progresses = progressRepository.findByMemberId(memberId);
            for (UserLevelProgress p : progresses) {
                progressMap.put(p.getLevel().getLevelId(), p);
            }
        }

        // 결과 DTO 조립
        List<LevelDto.Row> result = new ArrayList<>();

        for (Level level : levels) {

            UserLevelProgress p = progressMap.get(level.getLevelId());

            int progress = 0;
            String status = "학습 전";

            if (p != null) {
                progress = p.getProgress();
                if (progress >= 100) {
                    status = "완료";
                } else if (progress > 0) {
                    status = "진행 중";
                }
            }

            LevelDto.Row row = LevelDto.Row.builder()
                    .levelId(level.getLevelId())
                    .orderNumber(level.getOrderNumber())
                    .name(level.getName())
                    .description(level.getDescription())
                    .progress(progress)
                    .status(status)
                    .build();

            result.add(row);
        }

        return result;
    }

    /**
     * 단일 상세 조회
     */
    @Override
    @Transactional(readOnly = true)
    public LevelDto.Detail getDetail(Long levelId) {

        Level level = levelRepository.findById(levelId)
                .orElseThrow(() -> new RuntimeException("단계 정보를 찾을 수 없습니다."));

        return LevelDto.Detail.builder()
                .levelId(level.getLevelId())
                .name(level.getName())
                .description(level.getDescription())
                .orderNumber(level.getOrderNumber())
                .build();
    }

    /**
     * 관리자 등록
     */
    @Override
    public Long write(LevelDto.Write dto) {

        Level level = Level.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .orderNumber(dto.getOrderNumber())
                .build();

        levelRepository.save(level);

        return level.getLevelId();
    }

    /**
     * 관리자 수정
     */
    @Override
    public void edit(LevelDto.Edit dto) {

        Level level = levelRepository.findById(dto.getLevelId())
                .orElseThrow(() -> new RuntimeException("단계 정보를 찾을 수 없습니다."));

        level.edit(dto.getName(), dto.getDescription(), dto.getOrderNumber());

    }

    /**
     * 관리자 삭제
     */
    @Override
    public void delete(Long levelId) {
        levelRepository.deleteById(levelId);
    }

    /**
     * 회원 진행률 업데이트
     */
    @Override
    public void updateProgress(Long memberId, Long levelId, Integer progress) {

        LocalDateTime completedAt =
                (progress != null && progress >= 100) ? LocalDateTime.now() : null;

        // 기존 진행도 조회
        Optional<UserLevelProgress> optional =
                progressRepository.findByMemberIdAndLevelId(memberId, levelId);

        if (optional.isPresent()) {

            UserLevelProgress existing = optional.get();
            existing.updateProgress(progress, completedAt);

        } else {
            // 처음 저장하는 경우 새로 생성
            Level level = levelRepository.findById(levelId)
                    .orElseThrow(() -> new RuntimeException("단계 정보를 찾을 수 없습니다."));

            UserLevelProgress newProgress = UserLevelProgress.builder()
                    .progress(progress)
                    .completedAt(completedAt)
                    .level(level)
                    .member(null)
                    .build();

            progressRepository.save(newProgress);
        }
    }
}

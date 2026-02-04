package app.finup.layer.domain.quiz.service;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.member.repository.MemberRepository;
import app.finup.layer.domain.quiz.constant.QuizRedisKey;
import app.finup.layer.domain.quiz.dto.QuizDto;
import app.finup.layer.domain.quiz.entity.Quiz;
import app.finup.layer.domain.quiz.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * QuizService 구현 클래스
 * @author lky
 * @since 2025-12-17
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final MemberRepository memberRepository;


    @CacheEvict(
            value = QuizRedisKey.CACHE_QUESTION,
            key = "#rq.memberId"
    )
    @Override
    public void record(QuizDto.Add rq) {

        // [1] 회원 entity 조회
        Member member = memberRepository
                .findById(rq.getMemberId())
                .orElseThrow(() -> new BusinessException(AppStatus.MEMBER_NOT_FOUND));

        // [2] 수준 퀴즈 entity 생성
        Quiz quiz = Quiz.builder()
                .member(member)
                .score(rq.getScore())
                .build();

        // [3] 퀴즈 이력 저장 (기록)
        quizRepository.save(quiz);
    }
}

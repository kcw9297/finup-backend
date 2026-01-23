package app.finup.layer.domain.uploadfile.service;


import app.finup.common.enums.LogEmoji;
import app.finup.common.utils.LogUtils;
import app.finup.infra.file.storage.FileStorage;
import app.finup.layer.domain.uploadfile.dto.UploadFileDto;
import app.finup.layer.domain.uploadfile.entity.UploadFile;
import app.finup.layer.domain.uploadfile.repository.UploadFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UploadFileSchedulerServiceImpl implements UploadFileSchedulerService {

    private final UploadFileRepository uploadFileRepository;
    private final FileStorage fileStorage;

    @Override
    public void clearOrphan() {

        // [1] 고아 파일을 기록할 리스트
        List<String> removedList = new ArrayList<>();
        List<String> failedList = new ArrayList<>();
        List<Long> orphanIds = new ArrayList<>(); // 모든 파일의 번호 기록

        // [2] 고아 파일 조회
        List<UploadFile> orphans = uploadFileRepository.findByFileOwnerIsNull();

        // [3] 물리적 삭제 수행
        // 실패하는 경우에도 예외를 잡아 로그에 담아두고 기록
        orphans.forEach(dto -> {

            try {
                fileStorage.remove(dto.getFilePath());
                removedList.add(dto.getStoreName());

            } catch (Exception e) {
                LogUtils.showWarn(this.getClass(), "파일 삭제 실패! 파일명 : %s, 원인 : %s", dto.getStoreName(), e.getMessage());
                failedList.add(dto.getStoreName());

            } finally {
                orphanIds.add(dto.getUploadFileId());
            }
        });

        // [4] DB 삭제 (현재는 로그 기록만 하고, 성공/실패 무관하게 삭제)
        uploadFileRepository.deleteAllById(orphanIds);
        LogUtils.showInfo(
                this.getClass(), LogEmoji.OK,
                "고아(주인 없는) 파일 삭제 완료.\n삭제 성공 : %s, 삭제 실패 : %s", removedList, failedList
        );
    }
}

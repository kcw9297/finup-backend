package app.finup.layer.domain.uploadfile.service;

import app.finup.common.exception.BusinessException;
import app.finup.layer.domain.uploadfile.dto.UploadFileDto;
import app.finup.layer.domain.uploadfile.dto.UploadFileDtoMapper;
import app.finup.layer.domain.uploadfile.entity.UploadFile;
import app.finup.layer.domain.uploadfile.repository.UploadFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import app.finup.common.enums.AppStatus;
import org.springframework.web.multipart.MultipartFile;



import java.util.Collection;
import java.util.List;

/**
 * UploadFileService 구현 클래스
 * @author kcw
 * @since 2025-11-26
 */

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UploadFileServiceImpl implements UploadFileService {

    private final UploadFileRepository uploadFileRepository;

    @Override
    @Transactional(readOnly = true)
    public UploadFileDto.Detail getDetail(Long uploadFileId) {

        return uploadFileRepository
                .findById(uploadFileId)
                .map(UploadFileDtoMapper::toDetail)
                .orElseThrow(() -> new BusinessException(AppStatus.UPLOAD_FILE_NOT_FOUND));
    }


    @Override
    @Transactional(readOnly = true)
    public List<UploadFileDto.Detail> getOrphanList() {

        return uploadFileRepository
                .findByFileOwnerIsNull()
                .stream()
                .map(UploadFileDtoMapper::toDetail)
                .toList();
    }


    @Override
    public void removeAll(Collection<Long> uploadFileIds) {
        uploadFileRepository.deleteAllById(uploadFileIds);
    }

    /**
     * 파일 업로드(저장)
     *
     * @param file 업로드 파일
     * @return 저장된 파일 엔티티
     */
    @Override
    public UploadFile upload(MultipartFile file) {

        // [1] 파일 유효성 검사
        if (file == null || file.isEmpty()) {
            throw new BusinessException(AppStatus.UPLOAD_FILE_NOT_FOUND);

        }

        // [2] UploadFile 엔티티 생성 (필수 값 모두 세팅)
        UploadFile uploadFile = UploadFile.builder()
                .originalName(file.getOriginalFilename())
                .storeName(java.util.UUID.randomUUID() + "_" + file.getOriginalFilename())
                .size(file.getSize())
                .fileType(app.finup.layer.domain.uploadfile.enums.FileType.PROFILE)
                .filePath("/uploads/profile")
                .fileOwner(app.finup.layer.domain.uploadfile.enums.FileOwner.MEMBER)
                .build();

        // [3] DB 저장 후 반환
        return uploadFileRepository.save(uploadFile);
    }
}
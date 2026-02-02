package app.finup.layer.domain.uploadfile.service;

import app.finup.common.exception.BusinessException;
import app.finup.layer.domain.uploadfile.dto.UploadFileDto;
import app.finup.layer.domain.uploadfile.dto.UploadFileDtoMapper;
import app.finup.layer.domain.uploadfile.entity.UploadFile;
import app.finup.layer.domain.uploadfile.enums.FileOwner;
import app.finup.layer.domain.uploadfile.enums.FileType;
import app.finup.layer.domain.uploadfile.repository.UploadFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import app.finup.common.enums.AppStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.UUID;

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
    public void removeAll(Collection<Long> uploadFileIds) {
        uploadFileRepository.deleteAllById(uploadFileIds);
    }

}
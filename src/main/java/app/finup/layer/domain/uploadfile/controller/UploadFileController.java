package app.finup.layer.domain.uploadfile.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping(Url.UPLOAD_FILE)
@RequiredArgsConstructor
public class UploadFileController {

    @GetMapping("/{fileId}/download")
    public ResponseEntity<?> download(@PathVariable Long fileId) {

        /*
        // [1] 파일 조회
        FileDTO.Detail readDto = fileService.read(fileId);

        // [2] 다운로드를 위한 파일 byte array 추출
        byte[] fileBytes = fileManager.download(readDto.getStoreName(), readDto.getEntity().getName());
        String contentDisposition = "attachment; filename=\"%s\"".formatted(StrUtils.encodeToUTF8(readDto.getOriginalName()));

        // [3] header, body 내 정보 삽입 후 HTTP 응답 반환
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(fileBytes);

         */

        // 아직 미구현
        return Api.ok();
    }
}

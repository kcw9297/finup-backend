package app.finup.layer.domain.memberWordbook.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberWordbookDto {

    /**
     * 내 단어장 목록 조회 결과
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Row {
        private Long termId;
        private String name;
        private String description;
    }
    /**
     * 단어장 추가 요청
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Add {
        private Long termId;
    }

    /**
     * 단어장 삭제 요청
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Remove {
        private Long termId;
    }

    /**
     * 암기 단어
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MemorizedWord {
        private Long termId;
        private LocalDateTime memorizedAt;
        private String memorizeStatus;
    }
}


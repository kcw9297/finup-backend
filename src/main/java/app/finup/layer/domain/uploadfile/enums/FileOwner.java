package app.finup.layer.domain.uploadfile.enums;

import lombok.Getter;


/**
 * 파일 소유 엔티티 정보 상수 클래스
 * @author kcw
 * @since 2025-11-26
 */

@Getter
public enum FileOwner {

    MEMBER("member"),
    STUDY_WORD("studyWord"),
    UNKNOWN("unknown");

    private final String name;

    FileOwner(String name) {this.name = name;}
}

package app.finup.layer.domain.memberstudy.enums;

import lombok.Getter;

/**
 * 개념 학습 진도를 나타낼 열거형 상수 클래스
 */

@Getter
public enum StudyStatus {

    BEFORE("학습 전"),
    IN_PROGRESS("학습 중"),
    COMPLETED("학습 완료");

    private final String value;
    StudyStatus(String value) {
        this.value = value;
    }
}

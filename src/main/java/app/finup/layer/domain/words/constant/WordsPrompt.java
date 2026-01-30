package app.finup.layer.domain.words.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 주식 종목과 관련한 AI 프롬프트를 관리하는 상수
 * @author kcw
 * @since 2025-12-25
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WordsPrompt {

    // 사용 상수
    public static final String INPUT = "${INPUT}";
    public static final String INPUT_NEWS = "${INPUT_NEWS}";
    public static final String INPUT_WORDS = "${INPUT_WORDS}";
    public static final String RECOMMENDATION_AMOUNT = "${RECOMMENDATION_AMOUNT}";
    public static final String PREV_RECOMMENDATION_IDS = "${PREV_RECOMMENDATION_IDS}";


    // 뉴스 학습 단어 추천
    public static final String PROMPT_RECOMMENDATION_NEWS_WORDS = """
    
        ### 당신의 역할
            당신은 초보자를 위한 주식, 금융 정보를 쉽게 분석해 주는 서비스의 AI 입니다.
            현재 사용자가 보고 있는 뉴스 정보를 보고, 뉴스와 연관 있는 금융 용어를 추천해 주는 것이 목적입니다.
    
        ### 주어지는 데이터
            당신에게 현재 사용자가 보고 있는 뉴스의 제목, 본문 정보가 담긴 JSON 데이터와,
            사용자에게 추천할 수 있는 후보 단어 JSON 목록이 주어집니다.
            JSON 데이터의 형태는 아래와 같습니다.
    
        ### JSON 데이터 형태 - 뉴스
            { "title" : "...", "description" : "..." }
    
        ### 데이터 설명 - 뉴스
            - title : 뉴스 제목
            - description : 뉴스 본문
    
        ### JSON 데이터 형태 - 후보 단어
            [
                { "termId" : "...", "name" : "..." },
                { "termId" : "...", "name" : "..." },
                ...
            ]
    
        ### 데이터 설명 - 후보 단어
            - termId : 단어 번호
            - name : 단어 이름
    
        ### 당신의 목표
            현재 뉴스 정보를 보고, 뉴스와 연관성이 높은 단어를 후보 단어 중에서 선별해 주세요.
    
        ### 선정 규칙 [필수 준수]
            1. 선정할 단어의 총 개수는 정확히 "추천 단어 개수"와 일치해야 합니다.
            2. 절대로 동일한 termId를 중복으로 선택해선 안 됩니다.
            3. "이전 추천 단어 번호 목록"이 존재하는 경우, 과거에 추천되었던 단어는 추천 우선순위를 낮춥니다.
            4. 후보 단어 목록의 총 개수가 "추천 단어 개수"보다 적을 경우, 사용 가능한 모든 단어를 중복 없이 선택합니다.

        ### 출력 형식 [엄격히 준수]
            - 반드시 JSON 배열 형식으로만 출력하세요: ["termId1", "termId2", "termId3", ...]
            - "후보 단어 목록"에 실제로 존재하는 termId만 사용하세요.
            - 절대로 termId를 추측하거나 생성하지 마세요.
            - termId는 문자열 형태로 출력하세요. (숫자여도 따옴표로 감싸기)
            - 어떠한 설명이나 추가 텍스트도 포함하지 마세요.
            - JSON 배열 외 다른 내용은 절대 출력하지 마세요.
    
        ### 입력 뉴스 JSON:
        ${INPUT_NEWS}
    
        ### 입력 후보 단어 JSON:
        ${INPUT_WORDS}
    
        ### 추천 단어 개수:
        ${RECOMMENDATION_AMOUNT}
    
        ### 이전 추천 단어 번호 목록:
        ${PREV_RECOMMENDATION_IDS}
    """;

}

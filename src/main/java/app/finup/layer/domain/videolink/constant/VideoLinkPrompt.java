package app.finup.layer.domain.videolink.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 학습 영상과 관련한 AI 프롬프트를 관리하는 상수
 * @author kcw
 * @since 2025-12-16
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VideoLinkPrompt {

    public static final String PROMPT_RECOMMEND_RULE =
            """
                ### 키워드 생성 규칙 [필수 준수 사항]
                1. 키워드는 정확히 ${RECOMMEND_KEYWORDS_AMOUNT}개여야 합니다. 이것은 절대적인 요구사항이며 반드시 지켜야 합니다.
                2. 모든 키워드는 반드시 한글로 작성하세요.
                3. 영어 약어가 필요한 경우, 반드시 한글 용어를 먼저 쓰고 괄호 안에 영어 약어를 표기하세요.
                4. 각 키워드 내부에는 띄어쓰기를 사용하지 마세요. 단, 키워드와 키워드 사이는 공백으로 구분하세요.
                5. 키워드만 출력하고 다른 설명이나 출처, 부가 문구는 절대로 포함하면 안 됩니다.
                6. 아래 "출력 형식 예시"는 정확히 10개의 키워드로 구성되어 있습니다. 이 형식을 참고하여 요청된 개수만큼만 키워드를 생성하세요.
            
                ### 출력 형식 예시
                차트분석 캔들패턴 고점돌파 주식투자 기술적분석 이동평균선 볼린저밴드 상대강도지수(RSI) 거래량분석 꼬리분석
            
                ### 중요 알림
                키워드 개수가 ${RECOMMEND_KEYWORDS_AMOUNT}개가 아닌 경우 응답은 거부됩니다.
                공백으로 구분된 키워드를 세어보고 정확히 ${RECOMMEND_KEYWORDS_AMOUNT}개인지 확인한 후 출력하세요.
            """;


    public static final String PROMPT_RECOMMEND_KEYWORDS_RETRY =
            """
                [재시도 요청]
                아래는 이전에 생성된 키워드 목록입니다.
                이전 키워드와 중복되지 않도록 다른 관점이나 세부 주제에 초점을 맞춘 새로운 키워드를 생성해 주세요.
                이전 키워드: ${PREV_KEYWORDS}
            """;

    public static final String PROMPT_RECOMMEND_KEYWORDS_HOME =
            """
                당신은 주식 초보자들을 위한 투자, 경제 정보를 제공하는 서비스 사이트의 학습 영상 추천 AI입니다.
                페이지 홈(INDEX)에 게시될 유튜브 영상을 검색할 키워드를 생성해 주세요.

                ### 추천 방향
                홈 페이지는 투자를 처음 시작하는 사용자들이 가장 먼저 접하는 공간입니다.
                따라서 전문 용어보다는 일상에서 자주 접하는 보편적인 경제 개념을 중심으로 키워드를 구성하세요.
                딱딱한 금융 용어보다는 실생활과 연결된 쉽고 친근한 표현을 우선시하고,
                초보자도 부담 없이 시청할 수 있는 입문 수준의 내용을 다루는 영상을 찾을 수 있도록 키워드를 생성하세요.
            
                ${RULE}
            
                ${RETRY}
            
            """.replace("${RULE}", PROMPT_RECOMMEND_RULE);


    public static final String PROMPT_RECOMMEND_KEYWORDS_STUDY =
            """
                당신은 현재 학습명과 학습 요약본을 보고, 현재 학습에 적절한 유튜브 영상을 추천하는 AI입니다.
                현재 "학습 정보"의 학습 제목[name], 학습 요약 정보[summary]를 보고,
                해당 학습에 도움이 될 유튜브 영상을 검색할 키워드를 생성해 주세요.
            
                ${RULE}
            
                ### 학습 정보
                학습 제목 [name] : ${TARGET_STUDY_NAME}
                학습 요약 정보[summary]: ${TARGET_STUDY_SUMMARY}
            
                ${RETRY}
            
            """.replace("${RULE}", PROMPT_RECOMMEND_RULE);
}

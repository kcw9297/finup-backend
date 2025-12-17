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

    public static final String PROMPT_RECOMMEND_SENTENCE_RULE =
            """
                ### 당신의 역할
                당신은 주식 초보자들을 위한 투자, 경제 정보를 제공하는 서비스 사이트의 학습 영상 추천 AI입니다.
                사이트에 게시할 YouTube 영상을 검색하기 위해, 최적화된 하나의 자연스러운 검색 쿼리 문장을 생성해주세요.
            
                ### 문장 생성 규칙 [필수 준수 사항]
                1. 한글 중심으로 작성하되, 영어 전문용어는 '한글(영어)' 형식으로 병기하세요.
                2. 반드시 단일 문장으로만 작성하며, 15-20단어(공백 포함 60-80자) 이내로 제한하세요.
                3. 문장만 출력하고 설명, 출처, 부가 문구는 절대 포함하지 마세요.
                4. 자연스럽고 구체적인 검색 쿼리 형태로 작성하세요.
                5. YouTube 영상 제목에서 흔히 사용되는 표현을 활용하세요.
                6. 아래 출력 형식 예시는 당신이 출력해야 할 쿼리의 예시입니다. 반드시 형식을 준수하여 생성하세요.

                ### 출력 형식 예시
                주식투자 초보를 위한 기초 개념과 용어 설명
                재무제표(PER, PBR, ROE) 쉽게 이해하는 방법
                경제 뉴스에서 자주 나오는 금리와 인플레이션 개념
                ETF(상장지수펀드) 투자 시작하는 법
                배당주 투자 전략과 종목 선정 기준
                주식 차트 보는 법과 기술적 분석 입문
            """;


    public static final String PROMPT_RECOMMEND_SENTENCE_LATEST_RULE =
            """
                ### 이전 추천 검색 쿼리 내역
                아래는 이전에 생성된 검색 쿼리들입니다. 각 쿼리들은 쉼표(,)로 구분되어 있습니다.
                만약 이전 검색 쿼리가 있다면, 이전 쿼리와 의미가 중복되지 않도록 다른 관점이나 세부 주제로 새로운 검색 쿼리를 생성해 주세요.
                이전 검색 쿼리 : ${LATEST_SENTENCES}
            
                ### 중요: 다양성 확보 필수 조건
                반드시 아래 규칙을 따라 새로운 검색 쿼리를 생성하세요:
                1. 이전 쿼리와 동일하거나 유사한 주제는 절대 피하세요
                2. 표현 패턴을 다르게 구성하세요 (예: "~를 위한", "~하는 방법", "~알아보기" 등을 다양하게)
                3. 아래의 "다양성 확보 방법" 중 하나를 선택하여 차별화하세요:
            
                ### 다양성 확보 방법
                주제 전환: 이론 → 실습 → 사례 분석 → 심리/마인드
                관점 변경: 개인 투자 → 기관 분석 → 글로벌 시장 → 경제 지표
                형식 변경: 개념 설명 → 실전 팁 → 인터뷰/후기 → 뉴스 분석
            """;

    public static final String PROMPT_RECOMMEND_SENTENCE_HOME =
            """
                ${RULE}
            
                ### 추천 방향
                홈 페이지는 투자를 처음 시작하는 사용자들이 가장 먼저 접하는 공간입니다.
                따라서 전문 용어보다는 일상에서 자주 접하는 보편적인 경제 개념을 중심으로 문장을 생성해야 합니다.
                딱딱한 금융 용어보다는 실생활과 연결된 쉽고 친근한 표현을 우선시하고,
                초보자도 부담 없이 시청할 수 있는 입문 수준의 내용을 다루는 영상을 찾을 수 있도록 검색 쿼리를 생성해 주세요.
            
                ${RULE_LATEST_SENTENCES}
            
            """.replace("${RULE}", PROMPT_RECOMMEND_SENTENCE_RULE)
                    .replace("${RULE_LATEST_SENTENCES}", PROMPT_RECOMMEND_SENTENCE_LATEST_RULE);


    public static final String PROMPT_RECOMMEND_SENTENCE_STUDY =
            """
                ${RULE}
            
                ### 추가 지시
                당신은 현재 학습명과 학습 요약본을 보고, 현재 학습에 적절한 유튜브 영상을 추천해야 합니다.
                현재 "학습 정보"의 학습 제목[name], 학습 요약 정보[summary], 학습 본문[detail], 학습 수준[level]을 보고,
                해당 학습에 도움이 될 YouTube 영상을 검색하기 위해, 최적화된 하나의 자연스러운 검색 쿼리를 생성해주세요.
                학습 레벨은 1~5 사이의 정수이며, 낮을 수록 초보자에게 권장되는 강의입니다.

                ### 학습 정보
                학습 제목 [name] : ${TARGET_STUDY_NAME}
                학습 요약 정보[summary]: ${TARGET_STUDY_SUMMARY}
                학습 본문[detail]: ${TARGET_STUDY_DETAIL}
                학습 수준[level]: ${TARGET_STUDY_LEVEL}
            
                ${RULE_LATEST_SENTENCES}
            
            """.replace("${RULE}", PROMPT_RECOMMEND_SENTENCE_RULE)
                    .replace("${RULE_LATEST_SENTENCES}", PROMPT_RECOMMEND_SENTENCE_LATEST_RULE);
}

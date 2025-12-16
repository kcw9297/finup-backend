package app.finup.layer.domain.videolink.manager;

import app.finup.infra.ai.provider.ChatProvider;
import app.finup.layer.domain.videolink.constant.VideoLinkPrompt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * VideoLinkAiManager 구현 클래스
 * @author kcw
 * @since 2025-12-16
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoLinkAiManagerImpl implements VideoLinkAiManager {

    // 사용 의존성
    private final ChatProvider chatProvider;

    // 사용 상수
    public static final String RECOMMEND_KEYWORDS_AMOUNT = "10";

    @Override
    public String recommendKeywordsForHome() {

        // [1] 프롬프트 생성
        String prompt = VideoLinkPrompt.PROMPT_RECOMMEND_KEYWORDS_HOME
                .replace("${RECOMMEND_KEYWORDS_AMOUNT}", RECOMMEND_KEYWORDS_AMOUNT)
                .replace("${RETRY}", "");

        // [2] 프롬프트 생성 및, 생성된 추천 문자열 반환
        return chatProvider.query(prompt);
    }

    @Override
    public String recommendKeywordsForHome(String prevKeywords) {

        // [1] 프롬프트 생성
        String prompt = VideoLinkPrompt.PROMPT_RECOMMEND_KEYWORDS_HOME
                .replace("${RECOMMEND_KEYWORDS_AMOUNT}", RECOMMEND_KEYWORDS_AMOUNT)
                .replace("${RETRY}", VideoLinkPrompt.PROMPT_RECOMMEND_KEYWORDS_RETRY)
                .replace("${PREV_KEYWORDS}", prevKeywords);

        // [2] 프롬프트 생성 및, 생성된 추천 문자열 반환
        return chatProvider.query(prompt);
    }

    @Override
    public String recommendKeywordsForStudy() {

        // [1] 프롬프트 생성
        String prompt = VideoLinkPrompt.PROMPT_RECOMMEND_KEYWORDS_STUDY
                .replace("${RECOMMEND_KEYWORDS_AMOUNT}", RECOMMEND_KEYWORDS_AMOUNT)
                .replace("${RETRY}", "");

        // [2] 프롬프트 생성 및, 생성된 추천 문자열 반환
        return chatProvider.query(prompt);
    }

    @Override
    public String recommendKeywordsForStudy(String prevKeywords) {

        // [1] 프롬프트 생성
        String prompt = VideoLinkPrompt.PROMPT_RECOMMEND_KEYWORDS_HOME
                .replace("${RECOMMEND_KEYWORDS_AMOUNT}", RECOMMEND_KEYWORDS_AMOUNT)
                .replace("${RETRY}", VideoLinkPrompt.PROMPT_RECOMMEND_KEYWORDS_RETRY)
                .replace("${PREV_KEYWORDS}", prevKeywords);

        // [2] 프롬프트 생성 및, 생성된 추천 문자열 반환
        return chatProvider.query(prompt);
    }
}

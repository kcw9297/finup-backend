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

    @Override
    public String recommendSentenceForLogoutHome() {

        // [1] 프롬프트 생성
        String prompt = VideoLinkPrompt.PROMPT_RECOMMEND_SENTENCE_HOME
                .replace("${LATEST_SENTENCES}", "");

        // [2] 프롬프트 생성 및, 생성된 추천 문자열 반환
        return chatProvider.query(prompt);
    }

    @Override
    public String recommendSentenceForLoginHome(String latestSentences) {

        // [1] 프롬프트 생성
        String prompt = VideoLinkPrompt.PROMPT_RECOMMEND_SENTENCE_HOME
                .replace("${LATEST_SENTENCES}", latestSentences);

        // [2] 프롬프트 생성 및, 생성된 추천 문자열 반환
        return chatProvider.query(prompt);
    }


    @Override
    public String recommendSentenceForStudy(String studyName, String studySummary, String studyDetail, Integer studyLevel, String latestSentences) {

        // [1] 프롬프트 생성
        String prompt = VideoLinkPrompt.PROMPT_RECOMMEND_SENTENCE_STUDY
                .replace("${TARGET_STUDY_NAME}", studyName)
                .replace("${TARGET_STUDY_SUMMARY}", studySummary)
                .replace("${TARGET_STUDY_DETAIL}", studyDetail)
                .replace("${TARGET_STUDY_LEVEL}", String.valueOf(studyLevel))
                .replace("${LATEST_SENTENCES}", latestSentences);

        // [2] 프롬프트 생성 및, 생성된 추천 문자열 반환
        return chatProvider.query(prompt);
    }
}

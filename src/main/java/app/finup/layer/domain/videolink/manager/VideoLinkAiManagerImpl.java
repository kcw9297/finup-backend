package app.finup.layer.domain.videolink.manager;

import app.finup.common.utils.StrUtils;
import app.finup.infra.ai.provider.ChatProvider;
import app.finup.layer.domain.videolink.constant.VideoLinkPrompt;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


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
    public List<Long> recommendForStudy(String json) {

        // [1] 사용 프롬포트
        String prompt = VideoLinkPrompt.PROMPT_RECOMMEND_VIDEO_STUDY
                .replace("${INPUT}", json);

        // [2] 답변 기반 배열로 변환 및 반환
        String response = chatProvider.query(prompt);
        log.warn("AI RESPONSE JSON : {}", response);


        return StrUtils.fromJson(response, new TypeReference<>(){});
    }

}

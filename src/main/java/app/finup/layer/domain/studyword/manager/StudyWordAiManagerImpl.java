package app.finup.layer.domain.studyword.manager;

import app.finup.common.utils.StrUtils;
import app.finup.infra.ai.provider.ChatProvider;
import app.finup.layer.domain.studyword.constant.StudyWordPrompt;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * StudyWordAiManager 구현 클래스
 * @author kcw
 * @since 2025-12-16
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class StudyWordAiManagerImpl implements StudyWordAiManager {

    // 사용 의존성
    private final ChatProvider chatProvider;

    @Override
    public List<Long> recommendForStudy(String json) {

        // [1] 사용 프롬포트
        String prompt = StudyWordPrompt.PROMPT_RECOMMEND_WORD_STUDY
                .replace("${INPUT}", json);

        // [2] 답변 기반 배열로 변환 및 반환
        String response = chatProvider.query(prompt);
        String cleanResponse = StrUtils.removeMarkdownBlock(response);
        log.warn("AI RESPONSE JSON : {}, clean ver : {}", response, cleanResponse);
        return StrUtils.fromJson(cleanResponse, new TypeReference<>(){});
    }

}

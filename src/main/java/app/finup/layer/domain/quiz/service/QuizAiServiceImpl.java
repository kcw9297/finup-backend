package app.finup.layer.domain.quiz.service;

import app.finup.layer.domain.quiz.dto.QuizDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import app.finup.infra.ai.AiManager;
import app.finup.infra.ai.PromptTemplates;
import app.finup.layer.domain.quiz.dto.QuizDto;
import app.finup.layer.domain.studyword.dto.StudyWordDto;
import app.finup.layer.domain.studyword.service.StudyWordService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class QuizAiServiceImpl implements QuizAiService {

    private final StudyWordService studyWordService;
    private final ObjectMapper objectMapper;
    private final AiManager aiManager;

    @Override
    public List<QuizDto.Question> getQuizAi() {
        // [1] studyWord AI 데이터(랜덤 30개 단어)
        List<StudyWordDto.Quiz> studyWords = studyWordService.getQuizData();
        log.info("getQuizAi 단어 랜덤 뭐 가져오냐 : {}", studyWords);

        // [2] List -> Json으로 변환
        String studyWordJson;
        try {
            studyWordJson = objectMapper.writeValueAsString(studyWords);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("StudyWord JSON 변환 실패", e);
        }

        // [3] QuizAI 호출
        String prompt = PromptTemplates.QUIZ.replace("{studyWords}", studyWordJson);
        Map<String, Object> quizAi = aiManager.runJsonPrompt(prompt);

        log.info("퀴즈 Ai 어떤형식이지 : {}", quizAi);

        // [4] DTO 매핑
        List<QuizDto.Question> quizAiList = QuizDtoMapper.toQuestion(quizAi);

        return quizAiList;
    }
}

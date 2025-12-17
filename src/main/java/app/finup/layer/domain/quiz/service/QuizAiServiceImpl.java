package app.finup.layer.domain.quiz.service;

import app.finup.layer.domain.quiz.dto.QuizDtoMapper;
import app.finup.layer.domain.quiz.redis.QuizStorage;
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
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class QuizAiServiceImpl implements QuizAiService {

    private final StudyWordService studyWordService;
    private final ObjectMapper objectMapper;
    private final AiManager aiManager;
    private final QuizStorage quizStorage;

    @Override
    public List<QuizDto.Question> getQuizAi() {
        // [1] storage에서 quizPool 꺼내기
        List<List<QuizDto.Question>> quizPool = quizStorage.getQuiz();
        if (quizPool == null || quizPool.isEmpty()) {
            synchronized (this) {
                quizPool = quizStorage.getQuiz();
                if (quizPool == null || quizPool.isEmpty()) {
                    refreshQuizAi();
                    quizPool = quizStorage.getQuiz();
                }
            }
        }else{
            log.info("quizPool Redis에서 가져옴");
        }

        // [2] 랜덤으로 퀴즈 반환
        int idx = ThreadLocalRandom.current().nextInt(quizPool.size());
        List<QuizDto.Question> quiz = quizPool.get(idx);
        log.info("quizPool size={}", quizPool.size());
        log.info("random idx={}", idx);
        log.info("first question={}", quiz.get(0).getQuestion());

        return quiz;
    }

    @Override
    public void refreshQuizAi() {
        List<List<QuizDto.Question>> pool = new ArrayList<>();

        for (int i=0; i<5; i++) {
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

            // [3] QuizPool 생성
            String prompt = PromptTemplates.QUIZ.replace("{studyWords}", studyWordJson);
            Map<String, Object> quizAi = aiManager.runJsonPrompt(prompt); //AI 호출
            log.info("퀴즈 Ai : {}", quizAi);
            quizStorage.addQuiz(QuizDtoMapper.toQuestion(quizAi));
        }
    }
}

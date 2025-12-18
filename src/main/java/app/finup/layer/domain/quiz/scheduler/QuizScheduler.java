package app.finup.layer.domain.quiz.scheduler;

import app.finup.layer.domain.quiz.service.QuizAiService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Quiz 스케쥴러 자동 갱신
 * @author lky
 * @since 2025-12-17
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QuizScheduler {
    private final QuizAiService quizAiService;

    //30분 마다 새로운 수준 테스트 문제 생성
    @Scheduled(initialDelay = 5000, fixedDelay = 1000 * 60 * 30)
    public void refresh() {
        log.info("[SCHEDULER] 수준테스트 AI 문제 생성");
        quizAiService.refreshQuizAi();
    }
}
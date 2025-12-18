package app.finup.layer.domain.quiz.redis;

import app.finup.layer.domain.quiz.dto.QuizDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuizStorageImpl implements QuizStorage {

    @Qualifier("redisTemplate")
    private final RedisTemplate<String, Object> rt;
    private final ObjectMapper objectMapper;
    private static final int MAX_POOL_SIZE = 500;

    /* redis key */
    private static final String QUIZ = "quiz";

    @Override
    public void addQuiz(List<QuizDto.Question> quiz) {
        List<List<QuizDto.Question>> pool = (List<List<QuizDto.Question>>) rt.opsForValue().get(QUIZ);

        if (pool == null) {
            pool = new ArrayList<>();
        }

        if (pool.size() >= MAX_POOL_SIZE) {
            pool.remove(0); // 오래된 퀴즈 삭제
        }

        pool.add(quiz);
        rt.opsForValue().set(QUIZ, pool);
    }

    @Override
    public List<List<QuizDto.Question>> getQuiz(){
        List<List<?>> raw = (List<List<?>>) rt.opsForValue().get(QUIZ);
        if (raw == null) return Collections.emptyList();
        return raw.stream()
                .map(list ->
                        list.stream()
                                .map(o -> objectMapper.convertValue(o, QuizDto.Question.class))
                                .toList()
                )
                .toList();
    }
}

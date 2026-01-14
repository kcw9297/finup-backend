package app.finup.layer.domain.wordQuiz.service;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.layer.domain.wordQuiz.dto.WordQuizDto;
import app.finup.layer.domain.words.entity.Words;
import app.finup.layer.domain.words.repository.WordsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WordQuizServiceImpl implements WordQuizService {

    private final WordsRepository wordsRepository;

    @Override
    public WordQuizDto.Today getTodayQuiz() {

        // [1] 기준 단어 1개 조회 (랜덤)
        Words answerWord = wordsRepository.findRandom(PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElseThrow(() -> new BusinessException(AppStatus.WORD_QUIZ_NOT_FOUND));

        // [2] 오답 후보 조회 (정답 제외)
        List<String> wrongChoices =
                wordsRepository.findRandomNamesExclude(answerWord.getTermId()
                        , PageRequest.of(0, 1));

        // [3] 보기 조합
        List<String> choices = new ArrayList<>(wrongChoices);
        choices.add(answerWord.getName());
        Collections.shuffle(choices);

        return WordQuizDto.Today.builder()
                .termId(answerWord.getTermId())
                .question(answerWord.getDescription())
                .choices(choices)
                .build();
    }

    @Override
    public Boolean checkAnswer(WordQuizDto.Answer rq) {

        // [1] 기준 단어 조회
        Words word = wordsRepository.findByTermId(rq.getTermId())
                .orElseThrow(() -> new BusinessException(AppStatus.WORD_QUIZ_NOT_FOUND));

        // [2] 정답 비교
        return word.getName().equals(rq.getSelected());
    }
}

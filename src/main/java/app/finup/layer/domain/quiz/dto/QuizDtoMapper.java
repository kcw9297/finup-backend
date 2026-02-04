package app.finup.layer.domain.quiz.dto;

import app.finup.layer.domain.words.entity.Words;
import app.finup.layer.domain.words.enums.WordsLevel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Quiz AI 데이터 -> DTO 매퍼 클래스
 * @author kcw
 * @since 2026-01-11
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuizDtoMapper {

    // 랜덤 보기를 생성하기 위한 랜덤 수 생성 객체
    private static final Random RANDOM = new Random();

    // Question DTO 생성 (문제 보기, 정답 생성)
    public static QuizAiDto.Question toQuestion(
            Words selectedWord,
            Map<WordsLevel, Map<Long, Words>> levelWords,
            int choicesAmount) {

        // [1] 현재 문제 정보
        Long termId = selectedWord.getTermId();
        String description = selectedWord.getDescription();
        WordsLevel wordsLevel = selectedWord.getWordsLevel();
        List<Words> curLevelWords = new ArrayList<>(levelWords.get(wordsLevel).values());

        // [2] 오답 + 정답 Words 구성
        List<Words> choiceWords = RANDOM.ints(0, curLevelWords.size())
                .mapToObj(curLevelWords::get)
                .filter(word -> !Objects.equals(word.getTermId(), termId))
                .distinct()
                .limit(choicesAmount - 1)
                .collect(Collectors.toList());

        // [3] 현재 단어를 삽입하고 shuffle 수행
        choiceWords.add(selectedWord); // 현재 단어 삽입
        Collections.shuffle(choiceWords); // 셔플

        // [4] 단어명 리스트 및 정답 인덱스 추출
        List<String> choices = choiceWords.stream().map(Words::getName).toList();
        int answer = choiceWords.indexOf(selectedWord);

        // [5] DTO 변환 및 반환
        return QuizAiDto.Question.builder()
                .question(description)
                .choices(choices)
                .answer(answer)
                .build();
    }
}

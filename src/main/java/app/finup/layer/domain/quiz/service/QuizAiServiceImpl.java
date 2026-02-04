package app.finup.layer.domain.quiz.service;

import app.finup.common.utils.StrUtils;
import app.finup.infra.ai.ChatProvider;
import app.finup.layer.base.template.AiCodeTemplate;
import app.finup.layer.domain.quiz.constant.QuizPrompt;
import app.finup.layer.domain.quiz.constant.QuizRedisKey;
import app.finup.layer.domain.quiz.dto.QuizAiDto;
import app.finup.layer.domain.quiz.dto.QuizDtoMapper;
import app.finup.layer.domain.quiz.redis.QuizRedisStorage;
import app.finup.layer.domain.words.entity.Words;
import app.finup.layer.domain.words.enums.WordsLevel;
import app.finup.layer.domain.words.repository.WordsRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * QuizAiService 구현 클래스
 * @author kcw
 * @since 2026-01-10
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class QuizAiServiceImpl implements QuizAiService {

    // 사용 의존성
    private final WordsRepository wordsRepository;
    private final ChatProvider chatProvider;
    private final QuizRedisStorage quizRedisStorage;

    // 사용 상수
    private static final int LIMIT_WORDS_BEGINNER = 20;
    private static final int LIMIT_WORDS_INTERMEDIATE = 50;
    private static final int LIMIT_WORDS_ADVANCED = 30;
    private static final int AMOUNT_CHOICES = 4; // 선택 문항 개수
    private static final int QUESTION_AMOUNT = 10;
    private static final String QUESTION_RATIO = // 퀴즈 출제 비율
            "%s:%s:%s = %.1f:%.1f:%.1f".formatted(
                    WordsLevel.BEGINNER.name(), WordsLevel.INTERMEDIATE.name(), WordsLevel.ADVANCED.name(),
                    1.5, 2.5, 1.0
            );


    @Cacheable(
            value = QuizRedisKey.CACHE_QUESTION,
            key = "#memberId"
    )
    @Override
    public List<QuizAiDto.Question> generateQuestions(Long memberId) {

        // [1] 기존 퀴즈정보 조회 & 후보 단어 목록 조회
        List<Long> prev = quizRedisStorage.getPrevWordsIds(memberId); // 이전에 출제한 단어 목록
        List<Words> candidateWords = getCandidateWords(prev); // AI한테 제공할 후보 단어 목록 Stream

        // [2] Stream 기반, 후보 목록 생성
        List<GenerateQuestionRq> input = candidateWords.stream() // AI 입력으로 삽입할 DTO 목록
                .map(entity -> new GenerateQuestionRq(entity.getTermId(), entity.getName(), entity.getWordsLevel()))
                .toList();

        // Map<ID, Words> 후보 Map
        Map<Long, Words> candidates = candidateWords.stream()
                .collect(Collectors.toConcurrentMap(
                        Words::getTermId,
                        Function.identity()
                ));

        // Map<WordsLevel, Map<ID, Words>> 그룹 Map (레벨 별 단어 그룹화)
        // 모든 후보 단어를 이용해 상성 (추후 문제 생성 시 이용)
        Map<WordsLevel, Map<Long, Words>> levelWords = candidateWords.stream()
                .collect(Collectors.groupingBy(
                        Words::getWordsLevel,
                        Collectors.toMap(Words::getTermId, Function.identity())
                ));


        // [2] 프롬포트 생성
        // 프롬포트 파라미터
        Map<String, String> params = Map.of(
                QuizPrompt.INPUT, StrUtils.toJson(input),
                QuizPrompt.QUESTION_AMOUNT, String.valueOf(QUESTION_AMOUNT),
                QuizPrompt.QUESTION_RATIO, QUESTION_RATIO
        );

        // 프롬포트
        String prompt = StrUtils.fillPlaceholder(QuizPrompt.PROMPT_GENERATE_QUESTION, params);

        // [3] AI 추천 수행 및 결과 DTO 반환
        return AiCodeTemplate.recommendWithPrev(
                        chatProvider, prompt, candidates, Long.class, QUESTION_AMOUNT,
                        prevIds -> quizRedisStorage.storePrevWordsIds(memberId, prevIds)).stream()
                .map(selectedWords -> QuizDtoMapper.toQuestion(selectedWords, levelWords, AMOUNT_CHOICES))
                .toList();

    }

    // AI 프롬포트에 삽입할 후보 단어 조회 후, Stream 반환
    private List<Words> getCandidateWords(List<Long> prevWordsId) {

        // [1] 난이도 별 단어 랜덤 조회
        // ** 현재는 처리하지 않았으나 수십만개 이상의 데이터를 처리하는 경우, Hibernate 에서 IN, NOT IN 연산에 성능 이슈가 존재
        // ** 만약 나중에 그런 상황이 있는 경우 반드시 IN 연산할 컬렉션이 비어있으면 NULL로 전달할 것
        List<Words> beginnerWords = wordsRepository.findRandomByWordLevelWithExcludeIds(WordsLevel.BEGINNER, prevWordsId, LIMIT_WORDS_BEGINNER);
        List<Words> intermediateWords = wordsRepository.findRandomByWordLevelWithExcludeIds(WordsLevel.INTERMEDIATE, prevWordsId, LIMIT_WORDS_INTERMEDIATE);
        List<Words> advancedWords = wordsRepository.findRandomByWordLevelWithExcludeIds(WordsLevel.ADVANCED, prevWordsId, LIMIT_WORDS_ADVANCED);

        // [2] 세 단어를 모두 한 리스트로 합친 후 반환
        return Stream.of(beginnerWords, intermediateWords, advancedWords)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }


    // 퀴즈 생성 요청을 위해 임시로 사용하는 DTO (record)
    // 단어 뜻이 너무 길기도 하고, 단어 뜻 없이도 단어명으로도 충분히 유사도를 검증할 수 있음
    @Builder
    private record GenerateQuestionRq(Long termId, String name, WordsLevel wordsLevel) {}
}

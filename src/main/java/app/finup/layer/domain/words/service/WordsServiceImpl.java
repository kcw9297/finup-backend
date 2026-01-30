package app.finup.layer.domain.words.service;


import app.finup.common.enums.AppStatus;
import app.finup.common.enums.LogEmoji;
import app.finup.common.exception.BusinessException;
import app.finup.common.utils.AiUtils;
import app.finup.common.utils.LogUtils;
import app.finup.common.utils.ParallelUtils;
import app.finup.infra.ai.EmbeddingProvider;
import app.finup.infra.file.provider.CsvProvider;
import app.finup.infra.file.storage.FileStorage;
import app.finup.layer.domain.words.constant.WordsRedisKey;
import app.finup.layer.domain.words.entity.Words;
import app.finup.layer.domain.words.enums.WordsLevel;
import app.finup.layer.domain.words.redis.WordsRedisStorage;
import app.finup.layer.domain.words.dto.WordsDto;
import app.finup.layer.domain.words.dto.WordsDtoMapper;
import app.finup.layer.domain.words.repository.WordsRepository;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WordsServiceImpl implements WordsService {

    // 사용 의존성
    private final WordsRepository wordsRepository;
    private final WordsRedisStorage wordsRedisStorage;
    private final EmbeddingProvider embeddingProvider;
    private final CsvProvider csvProvider;
    private final FileStorage fileStorage;

    // 병렬 처리에 사용할 executor
    private final ExecutorService embeddingApiExecutor;

    // 사용 상수
    private static final String FILE_PATH_WORDS = "base/words_with_level.csv";
    private static final int MIN_AMOUNT_WORDS = 1000;
    private static final int CHUNK_INIT_WORD = 100;


    @Override
    @Transactional
    public void initWords() {

        // [1] 현재 단어 개수 조회
        long count = wordsRepository.count();

        // [2] 최소 단어 개수를 충족하면 파일을 읽지 않음
        if (count > MIN_AMOUNT_WORDS) {
            LogUtils.showInfo(this.getClass(), LogEmoji.OK, "이미 단어가 존재하여 초기화하지 않습니다. 현재 단어 개수 : %,d개", count);
            return;
        }

        // [3] 단어 개수가 충족되지 않는 경우, 단어 파일 로드 후, 임시 DTO 형태로 변경
        // BOM 문자가 존재하는 형태일 수 있으므로 VS Code에서 BOM 미포함 형태의 파일을 사용해야 함 (안그러면 로드 불가능)
        byte[] fileBytes = fileStorage.download(FILE_PATH_WORDS); // 파일 다운로드

        List<InitWordRequest> requests =
                csvProvider.extractRow(fileBytes) // 파일 추출
                        .stream()
                        .filter(this::isValidRow) // null 컬럼 값 존재 시 필터링
                        .map(this::toWordRequest)
                        .toList();


        // [4] 단어 임베딩을 위한 Map 생성 (Map<단어명, 임베딩텍스트>)
        Map<String, String> nameEmbeddingTextMap = requests.stream()
                .collect(Collectors.toConcurrentMap(
                        InitWordRequest::name,
                        request -> AiUtils.generateEmbeddingText(request.name, request.description)
                ));

        // [5] 요청을 보내기 위해 Map을 쪼갬
        List<Map<String, String>> chunks =
                Lists.partition(new ArrayList<>(nameEmbeddingTextMap.entrySet()), CHUNK_INIT_WORD)
                        .stream() // <단어명, 임베딩텍스트> 를 CHUNK_INIT_WORD개 단위로 분리
                        .map(entries ->
                                entries.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                        )
                        .toList();

        // [6] 병렬 처리로 임베딩 벡터 생성 요청 수행 (OpenAI API)
        Map<String, byte[]> nameEmbeddingMap =
                ParallelUtils.doParallelTask(
                                "OPENAI Embedding",
                                chunks,
                                embeddingProvider::generate,
                                new Semaphore(10),
                                embeddingApiExecutor
                        ).stream()
                        .flatMap(map -> map.entrySet().stream())
                        .collect(Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue));

        // [7] 엔티티 생성 및 저장 (기존 엔티티는 삭제)
        List<Words> entities = requests.stream()
                .map(request -> toEntity(request, nameEmbeddingMap.get(request.name)))
                .filter(entity -> Objects.nonNull(entity.getEmbedding()))
                .toList();

        wordsRepository.deleteAll();
        wordsRepository.saveAll(entities);
        LogUtils.showInfo(this.getClass(), LogEmoji.OK, "단어 초기화 완료. 초기화 단어 개수 : %,d개", entities.size());
    }

    // RowMap 내 null value 가 존재하는지 확인
    private boolean isValidRow(Map<String, String> rowMap) {

        return rowMap.values()
                .stream()
                .noneMatch(value -> Objects.isNull(value) || value.isBlank());
    }

    // RowMap 데이터를 Words Entity 클래스로 변환
    private InitWordRequest toWordRequest(Map<String, String> rowMap) {

        return InitWordRequest.builder()
                .name(rowMap.get("name"))
                .description(rowMap.get("description"))
                .wordsLevel(WordsLevel.valueOf(rowMap.get("level")))
                .build();
    }

    // RowMap 데이터를 Words Entity 클래스로 변환
    private Words toEntity(InitWordRequest request, byte[] embedding) {

        return Words.builder()
                .name(request.name)
                .description(request.description)
                .wordsLevel(request.wordsLevel)
                .embedding(embedding)
                .build();
    }

    // 임시로 사용할 DTO
    @Builder
    private record InitWordRequest(String name, String description, WordsLevel wordsLevel) {}



    @Cacheable(
            value = WordsRedisKey.CACHE_TODAY_WORDS,
            key = "'DEFAULT'"
    )
    @Override
    @Transactional(readOnly = true)
    public List<WordsDto.Row> getHomeWords() {
        return wordsRepository.findRandom(Pageable.ofSize(3))
                .stream()
                .map(WordsDtoMapper::toRow)
                .toList();
    }


    @Cacheable(
            value = WordsRedisKey.CACHE_SEARCH,
            key = "#keyword"
    )
    @Override
    @Transactional(readOnly = true)
    public List<WordsDto.Row> search(String keyword, Long memberId) {

        // [1] 검색어가 비어있는 경우, 유사도 검색이 불가능하므로 빈 결과반환
        if (!StringUtils.hasText(keyword)) return List.of();

        // [2] 검색 전, 현재 검색 단어 벡터화 후 검색 수행
        byte[] embedding = embeddingProvider.generate(keyword);
        List<WordsDto.Row> rows = wordsRepository
                .findWithSimilarByKeyword(keyword, embedding, 20)
                .stream()
                .map(WordsDtoMapper::toRow)
                .toList();

        // [3] 검색 결과가 있는 경우에만, 최근 검색어 저장
        if (!rows.isEmpty()) storeRecentWord(memberId, keyword);

        // [4] 검색 결과 반환
        return rows;
    }

    // REDIS 내 최근 단어 저장 시도
    private void storeRecentWord(Long memberId, String keyword) {

        try {
            wordsRedisStorage.storeRecentSearchKeyword(memberId, keyword);
        } catch (Exception e) {
            LogUtils.showWarn(this.getClass(), "단어 저장 실패! 원인 : %s", e.getMessage());
        }
    }


    @Override
    public List<String> getRecent(Long memberId) {
        return wordsRedisStorage.getRecentSearchKeywords(memberId, 10);
    }


    @Override
    public void clear(Long memberId) {
        wordsRedisStorage.clearRecentSearchKeywords(memberId);
    }


    @Override
    public void removeRecent(Long memberId, String keyword) {
        wordsRedisStorage.removeRecentSearchKeyword(memberId, keyword);
    }


    @Override
    @Transactional(readOnly = true)
    public WordsDto.Row getDetail(Long termId) {

        return wordsRepository.findById(termId)
                .map(WordsDtoMapper::toRow)
                .orElseThrow(() ->
                        new BusinessException(AppStatus.WORDS_NOT_FOUND)
                );
    }
}


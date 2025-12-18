package app.finup.layer.domain.words.service;

import app.finup.infra.ai.AiManager;
import app.finup.layer.domain.words.dto.WordsDto;
import app.finup.layer.domain.words.entity.Words;
import app.finup.layer.domain.words.repository.WordVectorRepository;
import app.finup.layer.domain.words.repository.WordsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WordsVectorServiceImpl implements WordsVectorService {
    private final AiManager aiManager;
    private final WordVectorRepository wordVectorRepository;
    private final WordsRepository wordsRepository;

    @Override
    public void ingestAll() {
        final int BATCH_SIZE = 50;
        int page = 0;
        while (true) {
            Page<Words> pageResult = wordsRepository.findAll(PageRequest.of(page, BATCH_SIZE));
            if(pageResult.isEmpty()){
                log.info("words ingest 완료");
                break;
            }
            for(Words word : pageResult.getContent()) {
                //벡터화 할 텍스트
                String text = word.getName()+":"+word.getDescription();

                //임베딩 생성 후 json배열 문자열로 변환
                String embeddingJson = aiManager.embedJson(text);

                //db저장
                if (embeddingJson == null || embeddingJson.isBlank()) {
                    log.error("embedding 생성 실패 wordId={}", word.getTermId());
                    continue;
                }
                wordVectorRepository.upsert(word.getTermId(), embeddingJson);
            }
            page++;
        }

    }

    @Override
    public List<WordsDto.Similarity> similarity(String queryEmbeddingJson, int topK) {
        List<WordsDto.Similarity> results = wordVectorRepository.search(queryEmbeddingJson, topK);
        log.info("result size={}", results.size());
        results.forEach(r ->
                log.info("word={}, score={}", r.getName(), r.getScore())
        );
        return results.stream()
                .limit(topK)
                .toList();
    }
}

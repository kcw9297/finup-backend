package app.finup.layer.domain.words.component;

import app.finup.layer.domain.words.service.WordsVectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class WordsVectorInitializer {

    private final WordsVectorService service;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("WordsVector ingest 시작");
        service.ingestAll();
        log.info("WordsVector ingest 종료");
    }
}


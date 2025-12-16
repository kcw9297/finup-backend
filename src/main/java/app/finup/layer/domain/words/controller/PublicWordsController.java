package app.finup.layer.domain.words.controller;

import app.finup.common.constant.Url;
import app.finup.layer.domain.words.dto.WordsDto;
import app.finup.layer.domain.words.service.WordsVectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(Url.WORDS_PUBLIC)
public class PublicWordsController {

    private final WordsVectorService vectorService;

    @PostMapping("/insert")
    public ResponseEntity<?> insert() {
        log.info(">>> ingest start");
        vectorService.ingestAll();
        log.info(">>> ingest end");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/similar")
    public List<WordsDto.Similarity> similar(@RequestBody String text){
        System.out.println(">>> controller hit, text=" + text);
        return vectorService.similarity(text, 10);
    }

}

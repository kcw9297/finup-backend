package app.finup.layer.domain.words.service;


import app.finup.layer.domain.words.dto.WordsDto;

import java.util.List;

public interface WordsVectorService {
    void ingestAll();
    List<WordsDto.Similarity> similarity(String text, int topK);
}

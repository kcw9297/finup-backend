package app.finup.infra.dictionary.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 금융용어 JSON → SQL Bulk Insert 파일 생성기
 * @author khj
 * @since 2025-12-10
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JsonToSqlConverter {
    private final ObjectMapper objectMapper;

    /**
     * JSON 파일을 읽어서 SQL INSERT 파일로 변환
     * @param jsonPath JSON 원본 파일 경로
     * @param sqlOutputPath SQL 출력 파일 경로
     */

    public void generateSqlFromJson(String jsonPath, String sqlOutputPath) {
        try {
            // [1] JSON 파일 읽기
            String json = Files.readString(Path.of(jsonPath));

            // [2] JSON → 트리 구조 변환
            JsonNode root = objectMapper.readTree(json);

            JsonNode items = root
                    .path("response")
                    .path("body")
                    .path("items")
                    .path("item");

            if (!items.isArray()) {
                log.error("JSON item 배열을 찾을 수 없습니다.");
                return;
            }
            log.info("총 {}건 용어 데이터 변환 시작", items.size());

            StringBuilder sb = new StringBuilder();

            // [3] 반복 처리
            for (JsonNode item : items) {
                String name = item.path("fnceDictNm").asText().trim();
                String desc = item.path("ksdFnceDictDescContent").asText();

                // SQL escape
                name = name.replace("'", "''");
                desc = desc.replace("'", "''");

                sb.append(String.format(
                        "INSERT INTO finance_dictionary (name, description) VALUES ('%s', '%s');\n",
                        name, desc
                ));
            }

            // [4] 파일로 저장
            Files.writeString(Path.of(sqlOutputPath), sb.toString());
            log.info("SQL 생성 완료 → {}", sqlOutputPath);

        } catch (IOException e) {
            log.error("금융용어 JSON → SQL 변환 실패: {}", e.getMessage());
        }
    }
}

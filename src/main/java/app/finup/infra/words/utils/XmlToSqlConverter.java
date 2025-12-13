package app.finup.infra.words.utils;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * 금융용어 XML → SQL Bulk Insert 변환기
 * @author khj
 * @since 2025-12-10
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class XmlToSqlConverter {
    private XmlMapper xmlMapper;

    public void generate(String xmlPath, String sqlOutputPath) {

        try {
            // [1] XML 파일 읽기
            String xml = Files.readString(Path.of(xmlPath));
            xml = StringEscapeUtils.unescapeHtml4(xml);

            // [2] XML -> DTO 파싱
            XmlAndJsonDtoUtil root = xmlMapper.readValue(xml, XmlAndJsonDtoUtil.class);

            List<XmlAndJsonDtoUtil.Item> items =
                    root.getBody() != null &&
                            root.getBody().getItems() != null &&
                            root.getBody().getItems().getItem() != null
                            ? root.getBody().getItems().getItem()
                            : List.of();

            log.info("총 {}건 XML 항목 변환 시작", items.size());

            StringBuilder sb = new StringBuilder();

            // [3] SQL 생성
            for (XmlAndJsonDtoUtil.Item i : items) {
                String name = safe(i.getFnceDictNm());
                String desc = safe(i.getKsdFnceDictDescContent());

                sb.append(String.format(
                        "INSERT INTO words (name, description) VALUES ('%s', '%s');\n",
                        name, desc
                ));
            }

            // [4] 파일로 저장
            Files.writeString(Path.of(sqlOutputPath), sb.toString());
            log.info("SQL 파일 생성 완료 -> {}", sqlOutputPath);

        } catch (IOException ie) {
            log.error("XML -> SQL 생성 실패: {}", ie.getMessage());
        }
    }


    // SQL 문자열 escape 처리
    private String safe(String s) {
        if (s == null) return "";
        return s.replace("'", "''").trim();
    }
}

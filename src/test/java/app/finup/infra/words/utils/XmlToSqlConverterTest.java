package app.finup.infra.words.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@Slf4j
@SpringBootTest
class XmlToSqlConverterTest {

    @Autowired
    private XmlToSqlConverter xmlToSqlConverter;

    @Test
    void generateSql() {
        xmlToSqlConverter.generate(
                "/src/main/resources/용어사전.xml",
                "/src/main/resources/용어사전.sql"
        );
    }
}
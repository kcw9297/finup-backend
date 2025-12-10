package app.finup.infra.dictionary.utils;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

/**
 * 예탁결제원 금융용어 XML 응답 매핑용 DTO 유틸
 * (외부 응답 전용, 내부 도메인과는 분리)
 * @author khj
 * @since 2025-12-10
 */

@Data
@JacksonXmlRootElement(localName = "response")
public class XmlDtoUtil {
    @JacksonXmlProperty(localName = "header")
    private Header header;

    @JacksonXmlProperty(localName = "body")
    private Body body;

    @Data
    public static class Header {
        @JacksonXmlProperty(localName = "resultCode")
        private String resultCode;

        @JacksonXmlProperty(localName = "resultMsg")
        private String resultMsg;
    }

    @Data
    public static class Body {
        @JacksonXmlProperty(localName = "items")
        private Items items;

        @JacksonXmlProperty(localName = "numOfRows")
        private Integer numOfRows;

        @JacksonXmlProperty(localName = "pageNo")
        private Integer pageNo;

        @JacksonXmlProperty(localName = "totalCount")
        private Integer totalCount;
    }


    @Data
    public static class Items {
        // <items><item>...</item></items> 구조에서 item 리스트
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "item")
        private List<Item> item;
    }

    @Data
    public static class Item {
        @JacksonXmlProperty(localName = "fnceDictNm")
        private String fnceDictNm;

        @JacksonXmlProperty(localName = "ksdFnceDictDescContent")
        private String ksdFnceDictDescContent;
    }
}


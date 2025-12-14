package app.finup.layer.domain.indexMarket.api;

import app.finup.layer.domain.indexMarket.dto.IndexMarketDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class IndexMarketApiClientImpl implements IndexMarketApiClient {
    // 금융위원회 지수 API 전용 WebClient
    @Qualifier("openPortalClient")
    private final WebClient openPortalClient;

    // 공공데이터포털 인증 키
    @Value("${API_OPENPORTAL_KEY}")
    private String openPortalKey;

    // 주가지수 시세 조회 API (단건)
    public IndexMarketDto.ApiRow fetchIndex(String indexName, String baseDate) {
        try {
            String json = openPortalClient.get()
                    .uri(uri -> uri
                            .queryParam("serviceKey", openPortalKey)
                            .queryParam("resultType", "json")
                            .queryParam("numOfRows", 1)
                            .queryParam("pageNo", 1)
                            .queryParam("idxNm", indexName)
                            .queryParam("basDt", baseDate)
                            .build()
                    )
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return parseOpenPortalJson(json);
        } catch (Exception e) {
            log.error("지수 API 호출 실패 ({} / {}): {}", indexName, baseDate, e.getMessage());
            return null;
        }
    }

    // 지수 API JSON 응답 파싱
    private IndexMarketDto.ApiRow parseOpenPortalJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);

            JsonNode itemNode = root.path("response")
                    .path("body")
                    .path("items")
                    .path("item");

            if (!itemNode.isArray() || itemNode.isEmpty()) {
                return null;
            }
            return mapper.treeToValue(
                    itemNode.get(0),
                    IndexMarketDto.ApiRow.class
            );
        } catch (Exception e) {
            log.error("지수 JSON 파싱 실패: {}", e.getMessage());
            return null;
        }
    }
}
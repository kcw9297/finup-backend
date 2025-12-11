package app.finup.infra.dictionary.provider;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ProviderException;
import app.finup.infra.dictionary.dto.DictionaryProviderDto;
import app.finup.infra.dictionary.dto.KsdApiResponse;
import app.finup.infra.dictionary.utils.XmlAndJsonDtoUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class KSDDictionaryProvider implements DictionaryProvider {

    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper;


    @Value("${api.finance-dict.key}")
    private String apiKey;

    @Override
    public List<DictionaryProviderDto.Row> fetchTerms() {
        // [1] API 호출
        String rp = requestApi();
        log.info("★ KSD 응답 원문:\n{}", rp);

        // [2] JSON 파싱 (필수)
        XmlAndJsonDtoUtil parsed = parseResponse(rp);

        // [3] item 리스트 안전 추출
        List<XmlAndJsonDtoUtil.Item> items = extractItems(parsed);

        // [4] 내부 공용 DTO로 변환
        return items.stream()
                .map(this::convertToRow)
                .toList();
    }

    /**
     * [1] 외부 KSD API 호출
     */
    private String requestApi() {
        try {
            String url = "https://api.seibro.or.kr/openapi/service/FnTermSvc/getFinancialTermMeaning"
                    + "?serviceKey=" + apiKey
                    + "&pageNo=1&numOfRows=12000";

            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            log.error("KSD API 호출 실패: {}", e.getMessage());
            throw new ProviderException(AppStatus.FINANCE_DICT_API_FAILED, e);
        }
    }

    /**
     * [2] API 응답(JSON) → DTO 변환
     */

    private XmlAndJsonDtoUtil parseResponse(String json) {
        try {
            // [1] JSON 대응 -> Object Mapper 사용
            ObjectMapper objectMapper = new ObjectMapper();

            // [2] JSON 최상단 response 래퍼 파싱
            KsdApiResponse wrapper = objectMapper.readValue(json, KsdApiResponse.class);

            // [3] 실제 header/body/items가 들어 있는 부분 꺼내기
            return wrapper.getResponse();
        } catch (Exception jsonEx) {
            // [4] JSON 파싱 실패 시 XML로 재시도
            try {
                return xmlMapper.readValue(json, XmlAndJsonDtoUtil.class);
            } catch (Exception xmlEx) {
                log.error("KSD 응답 파싱 실패 json-xml: {}", xmlEx.getMessage());
                throw new ProviderException(AppStatus.FINANCE_DICT_API_FAILED, xmlEx);
            }
        }
    }

    /**
     * [3] item 리스트 안전 추출
     */
    private List<XmlAndJsonDtoUtil.Item> extractItems(XmlAndJsonDtoUtil dto) {
        if (dto == null ||
                dto.getBody() == null ||
                dto.getBody().getItems() == null ||
                dto.getBody().getItems().getItem() == null) {

            return List.of();
        }

        return dto.getBody().getItems().getItem();
    }

    /**
     * [4] Item → 내부 Row DTO 변환 + HTML 정제
     */
    private DictionaryProviderDto.Row convertToRow(XmlAndJsonDtoUtil.Item item) {
        return DictionaryProviderDto.Row.builder()
                .name(HtmlCleaner.escapeSql(HtmlCleaner.clean(item.getFnceDictNm())
                        )
                )
                .description(HtmlCleaner.escapeSql(HtmlCleaner.clean(item.getKsdFnceDictDescContent())
                        )
                )
                .build();
    }
}

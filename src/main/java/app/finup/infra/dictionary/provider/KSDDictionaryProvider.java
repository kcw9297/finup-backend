package app.finup.infra.dictionary.provider;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ProviderException;
import app.finup.infra.dictionary.dto.DictionaryProviderDto;
import app.finup.infra.dictionary.utils.XmlDtoUtil;
import app.finup.layer.domain.financeDictionary.dto.FinanceDictionaryDto;
import app.finup.layer.domain.financeDictionary.dto.FinanceDictionaryDtoMapper;
import app.finup.layer.domain.financeDictionary.entity.FinanceDictionary;
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
        String url = "https://api.seibro.or.kr/openapi/service/FnTermSvc/getFinancialTermMeaning"
                + "?serviceKey=" + apiKey
                + "&pageNo=1&numOfRows=9999";

        try {
            // [1] API 호출
            String xml = restTemplate.getForObject(url, String.class);

            // [2] XML -> DTO 변환
            XmlDtoUtil rp = xmlMapper.readValue(xml, XmlDtoUtil.class);

            // [3] item 리스트 안전하게 꺼내기
            List<XmlDtoUtil.Item> items =
                    rp.getBody() != null &&
                    rp.getBody().getItems() != null &&
                    rp.getBody().getItems().getItem() != null
                    ? rp.getBody().getItems().getItem() : List.of();

            // [4] 내부에서 사용하는 DTO(DictionaryProviderDto.Row)로 변환

            return items.stream()
                    .map(item -> DictionaryProviderDto.Row.builder()
                            .name(item.getFnceDictNm())
                            .description(item.getKsdFnceDictDescContent())
                            .build())
                    .toList();

        } catch (Exception e) {
            log.error("KSD 금융용어 API 호출 실패: {}" , e.getMessage());
            throw new ProviderException(AppStatus.FINANCE_DICT_API_FAILED, e);
        }

    }
}

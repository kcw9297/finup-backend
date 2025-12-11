package app.finup.layer.domain.stock.service;

import app.finup.infra.ai.AiManager;
import app.finup.infra.ai.PromptTemplates;
import app.finup.layer.domain.stock.dto.StockDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;


/**
 * StockAiService 구현 클래스
 * @author lky
 * @since 2025-12-03
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StockAiServiceImpl implements StockAiService {

    private final AiManager aiManager;
    private final ObjectMapper objectMapper;

    //종목 분석 Ai 데이터 가져오기
    @Override
    public Map<String, Object> getStockAi(StockDto.Detail detail) {
        try {
            // 1) Detail → 구조화된 Map(JSON 구조)
            Map<String, Object> structured = convertDetailToStructuredJson(detail);

            // 2) JSON 문자열로 변환
            String detailJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(structured);

            System.out.println("AI 원본 종목데이터: "+detailJson);

            // 3) 템플릿에 삽입
            String prompt = PromptTemplates.STOCK_ANALYSIS
                    .replace("{detail}", detailJson);

            // 4) GPT JSON 요청
            Map<String, Object> detailAi = aiManager.runJsonPrompt(prompt);

            return detailAi;

        } catch (Exception e) {
            log.error("AI 분석 생성 실패", e);
            return Map.of("error", "AI 분석 실패");
        }
    }
    private Map<String, Object> convertDetailToStructuredJson(StockDto.Detail detail) {
        return Map.of(
                "basic", Map.of(
                        "htsKorIsnm", detail.getHtsKorIsnm(),
                        "stckShrnIscd", detail.getStckShrnIscd(),
                        "stckPrpr", detail.getStckPrpr(),
                        "rprsMrktKorName", detail.getRprsMrktKorName(),
                        "bstpKorIsnm", detail.getBstpKorIsnm(),
                        "stckFcam", detail.getStckFcam(),
                        "htsAvls", detail.getHtsAvls(),
                        "lstnStcn", detail.getLstnStcn()
                ),
                "price", Map.of(
                        "w52Hgpr", detail.getW52Hgpr(),
                        "w52Lwpr", detail.getW52Lwpr(),
                        "d250Hgpr", detail.getD250Hgpr(),
                        "d250Lwpr", detail.getD250Lwpr()
                ),
                "valuation", Map.of(
                        "per", detail.getPer(),
                        "pbr", detail.getPbr(),
                        "eps", detail.getEps(),
                        "bps", detail.getBps()
                ),
                "supply", Map.of(
                        "frgnNtbyQty", detail.getFrgnNtbyQty(),
                        "pgtrNtbyQty", detail.getPgtrNtbyQty(),
                        "htsFrgnEhrt", detail.getHtsFrgnEhrt(),
                        "volTnrt", detail.getVolTnrt()
                ),
                "risk", Map.of(
                        "tempStopYn", detail.getTempStopYn(),
                        "invtCafulYn", detail.getInvtCafulYn(),
                        "shortOverYn", detail.getShortOverYn(),
                        "mangIssuClsCode", detail.getMangIssuClsCode()
                )
        );
    }


}

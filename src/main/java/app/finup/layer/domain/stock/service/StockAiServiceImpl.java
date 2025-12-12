package app.finup.layer.domain.stock.service;

import app.finup.infra.ai.AiManager;
import app.finup.infra.ai.PromptTemplates;
import app.finup.layer.domain.stock.api.StockApiClient;
import app.finup.layer.domain.stock.dto.StockDto;
import app.finup.layer.domain.stock.dto.StockDtoMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
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
    private final StockApiClient stockApiClient;

    //종목 분석 AI 데이터 가져오기(AI 정리, 추천 영상)
    @Override
    public Map<String, Object> getStockAi(StockDto.Detail detail) {
        try {
            // 1) Detail → 구조화된 Map(JSON 구조)
            Map<String, Object> structured = convertDetailToStructuredJson(detail);

            // 2) JSON 문자열로 변환
            String detailJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(structured);

            // 3) 템플릿에 삽입
            String prompt = PromptTemplates.STOCK_ANALYSIS
                    .replace("{detail}", detailJson);

            // 4) GPT JSON 요청
            Map<String, Object> detailAi = aiManager.runJsonPrompt(prompt);

            // 5) keyword List 생성
            List<String> keywordList = (List<String>) detailAi.get("youtubeKeywords");
            System.out.println("키워드: "+keywordList);

            // 6) 유튜브 데이터 요청
            List<StockDto.YoutubeVideo> youtube = getYoutubeVideo(keywordList);
            System.out.println("유튜브: "+youtube);

            return Map.of(
                    "detailAi", detailAi,
                    "youtube", youtube
                    );
            //return detailAi;

        } catch (Exception e) {
            log.error("AI 분석 생성 실패", e);
            return Map.of("error", "AI 분석 실패");
        }
    }

    //Detail DTO Json 형식으로 변환
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

    private List<StockDto.YoutubeVideo> getYoutubeVideo(List<String> keywordList) {
        List<StockDto.YoutubeVideo> youtubeList = new ArrayList<>();

        for (String keyword : keywordList) {
            StockDto.YoutubeSearchResponse response = stockApiClient.fetchYoutubeVideo(keyword);
            StockDto.YoutubeVideo youtube = StockDtoMapper.toYoutube(keyword, response);
            youtubeList.add(youtube);
        }
        return youtubeList;
    }

}

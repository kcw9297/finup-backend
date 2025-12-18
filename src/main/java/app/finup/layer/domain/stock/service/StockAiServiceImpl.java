package app.finup.layer.domain.stock.service;

import app.finup.infra.ai.AiManager;
import app.finup.infra.ai.PromptTemplates;
import app.finup.layer.domain.stock.api.StockApiClient;
import app.finup.layer.domain.stock.dto.StockDto;
import app.finup.layer.domain.stock.dto.StockDtoMapper;
import app.finup.layer.domain.stock.redis.StockStorage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final StockStorage stockStorage;

    //종목 분석 AI 데이터 가져오기(AI 정리, 추천 영상)
    @Override
    public Map<String, Object> getStockAi(String code, StockDto.Detail detail) {
        //1) 종목 AI 분석
        Map<String, Object> detailAi = stockStorage.getDetailAi(code);
        if (detailAi == null){
            refreshDetailAi(code, detail);
            detailAi = stockStorage.getDetailAi(code);
        }else{
            log.info("종목 AI분석 Redis에서 가져옴");
        }
        if (detailAi == null) {
            return Map.of(
                    "detailAi", Map.of("error", "AI 분석 실패"),
                    "youtube", List.of()
            );
        }

        //2) 유튜브 검색 키워드
        String keyword = (String) detailAi.get("youtubeKeyword");
        if (keyword == null || keyword.isBlank()) {
            log.warn("youtubeKeyword 없음, 기본 키워드 사용");
            keyword = detail.getHtsKorIsnm(); // 종목명
        }

        //3) 유튜브 추천 영상
        List<StockDto.YoutubeVideo> youtube = stockStorage.getYoutube(keyword);
        if (youtube == null) {
            refreshYoutube(keyword);
            youtube = stockStorage.getYoutube(keyword);
        }else{
            log.info("종목 추천영상 Redis에서 가져옴");
        }

        //4) 반환
        return Map.of(
                "detailAi", detailAi,
                "youtube", youtube
        );

    }

    //종목 AI분석 갱신하기
    @Override
    public void refreshDetailAi(String code, StockDto.Detail detail){
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

            log.info("종목 AI분석 갱신함 code={}", code);
            stockStorage.setDetailAi(code, detailAi);

        } catch (Exception e) {
            log.error("AI 분석 생성 실패", e);
        }
    }

    //종목 추천영상 갱신하기
    @Override
    public void refreshYoutube(String keyword){
        List<StockDto.YoutubeVideo> youtube = getYoutubeVideo(keyword);
        log.info("종목 추천영상 갱신함"+youtube);
        stockStorage.setYoutube(keyword, youtube);
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

    private List<StockDto.YoutubeVideo> getYoutubeVideo(String keyword) {
        StockDto.YoutubeSearchResponse response = stockApiClient.fetchYoutubeVideo(keyword);
        if (response == null || response.getItems() == null) return List.of();
        List<StockDto.YoutubeVideo> youtubeList = StockDtoMapper.toYoutubeList(keyword, response);
        return youtubeList;
    }

}

package app.finup.layer.base.init;


import com.example.demo.common.utils.LogUtils;
import com.example.demo.layer.domain.stock.service.StockService;
import com.example.demo.layer.domain.words.service.WordsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 초기화를 처리하는 클래스
 * @author kcw
 * @since 2026-01-08
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class AppInitializer {

    // 사용 의존성
    private final StockService stockService;
    private final WordsService wordsService;

    // 앱 시작 직전 초기화 로직 (모든 Spring Bean 초기화 완료 시점)
    @EventListener(ApplicationReadyEvent.class)
    public void init() {

        // 초기화 - 주식 파일 추출 (추출된 결과가 없을 시 최초 1회)
        LogUtils.runMethodAndShowCostLog("주식 파일 추출", stockService::initStockFile);

        // 초기화 - 주식 AT 발급
        LogUtils.runMethodAndShowCostLog("주식 API AccessToken 발급", stockService::issueToken);

        // 초기화 - 사전 단어 파일 추출 및 저장 (단어가 없을 시 최초 1회)
        LogUtils.runMethodAndShowCostLog("사전 단어 초기화", wordsService::initWords);
    }

}

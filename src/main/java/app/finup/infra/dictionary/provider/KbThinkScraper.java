package app.finup.infra.dictionary.provider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * KB Think 금융용어 스크래퍼 Provider / DTO (일단 병합) -> 셀레니움 크롬 방식으로 바꿈
 * @author khj
 * @since 2025-12-11
 */

@Slf4j
@Component
public class KbThinkScraper {
    private static final String BASE_URL = "https://kbthink.com/dictionary.html?pageNo=";

    private WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        return new ChromeDriver(options);
    }



    /**
     * 특정 페이지에서 용어 리스트(제목+링크+요약)를 가져옴
     * @param page 페이지 번호
     */
    /**
     * 목록 페이지 크롤링
     */
    public List<TermSummary> fetchList(Integer page) {

        List<TermSummary> termList = new ArrayList<>();
        WebDriver driver = createDriver();

        try {
            String url = BASE_URL + page;
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // [1] 페이지 완전 로딩
            wait.until(webDriver ->
                    ((JavascriptExecutor) webDriver).executeScript("return document.readyState")
                            .equals("complete"));

            log.warn("=== AFTER PAGE LOAD ===");
            // log.warn(driver.getPageSource());   // 강제 출력
            log.warn("=== END ===");

            // [2] 실제 리스트가 생길 때까지 기다림 (검사 버튼 효과와 동일)
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("div.dictionary-list-comp__list")
            ));

            List<WebElement> items = driver.findElements(By.cssSelector("div.dictionary-list-comp__list"));

            for (WebElement item : items) {

                String name = item.findElement(By.cssSelector(".dictionary-list-comp__list__title")).getText();

                String summary = "";
                List<WebElement> enNames = item.findElements(By.cssSelector(".dictionary-list-comp__list__en"));
                if (!enNames.isEmpty()) summary = enNames.get(0).getText();

                String desc = item.findElement(By.cssSelector(".dictionary-list-comp__list__desc")).getText();

                // 상세 링크 가져오기
                WebElement aTag = item.findElement(By.cssSelector("a[href]"));
                String detailUrl = aTag.getDomAttribute("href");

                if (detailUrl != null && detailUrl.startsWith("/")) {
                    detailUrl = "https://kbthink.com" + detailUrl;
                }

                // KBThink는 detail 링크가 없을 수 있으므로 name을 key로 저장
                termList.add(new TermSummary(name, summary + " " + desc, detailUrl));
            }

        } catch (Exception e) {
            log.error("fetchList Error:", e);
            try {
                log.warn("=== PAGE SOURCE START ===");
//                log.warn(driver.getPageSource());
                log.warn("=== PAGE SOURCE END ===");
            } catch (Exception ignore) {}
        } finally {
            driver.quit();
        }

        return termList;
    }


    /**
     * 상세페이지 크롤링 (Selenium)
     * @param detailUrl 상세 페이지 URL
     */
    public String fetchDetail(String detailUrl) {

        WebDriver driver = createDriver();

        try {
            // [1] 드라이버에 상세 페이지 전달
            driver.get(detailUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            // [2] 페이지 로딩 완료
            wait.until(webDriver ->
                    ((JavascriptExecutor) webDriver)
                            .executeScript("return document.readyState")
                            .equals("complete"));

            // [3] 실제 상세 설명이 렌더링될 때까지 기다림
            wait.until(ExpectedConditions.presenceOfElementLocated(
//                    By.cssSelector("div.dictionary-view-comp__detail_desc")
                    By.cssSelector("div[class*='detail']")
            ));

            WebElement content = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector("div[class*='detail']")     // 핵심: 부분 매칭
                    )
            );

            return sanitize(content.getText());

        } catch (Exception e) {
            log.error("fetchDetail Error:", e);
            // log.debug("DETAIL PAGE SOURCE:\n{}", driver.getPageSource());
            return "";
        } finally {
            driver.quit();
        }
    }


    /**
     * KBThink 내용이 HTML이기 때문에 불필요한 태그 제거/트리밍 정리
     */
    private String sanitize(String html) {
        if (html == null) return "";
        return html.replaceAll("\\s+", " ").trim();
    }

    /**
     * 리스트 페이지에서 사용하는 DTO
     */

    @Getter
    @AllArgsConstructor
    public static class TermSummary {
        private String name;
        private String summary;
        private String detailUrl;
    }
}

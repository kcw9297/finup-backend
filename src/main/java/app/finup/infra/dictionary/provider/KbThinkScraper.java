package app.finup.infra.dictionary.provider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
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
        options.addArguments("--headless=new");
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
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ul.list_dic")));

            List<WebElement> items = driver.findElements(By.cssSelector("ul.list_dic > li"));

            for (WebElement item : items) {

                String name = item.findElement(By.tagName("strong")).getText();

                String summary = "";
                List<WebElement> pTags = item.findElements(By.tagName("p"));
                if (!pTags.isEmpty()) {
                    summary = pTags.get(0).getText();
                }

                WebElement link = item.findElement(By.tagName("a"));
                String detailUrl = link.getDomAttribute("href");

                termList.add(new TermSummary(name, summary, detailUrl));
            }

        } catch (Exception e) {
            log.error("fetchList Error:", e);
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
            driver.get(detailUrl);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".entry")));

            WebElement content = driver.findElement(By.cssSelector(".entry"));
            return sanitize(content.getText());
        } catch (Exception e) {
            log.error("fetchDetail Error:", e);
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

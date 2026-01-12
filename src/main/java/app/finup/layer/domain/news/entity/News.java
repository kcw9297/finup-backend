package app.finup.layer.domain.news.entity;

import app.finup.layer.domain.news.enums.NewsType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "news",
        uniqueConstraints = { // 복합 UK (뉴스제목, 뉴스타입, 뉴스코드)
                @UniqueConstraint(columnNames = {"title", "newsType", "stock_code"})
        }
)
@DynamicUpdate
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long newsId;

    @Column(nullable = false)
    private String title; // 기사 제목

    private String summary; // 기사 제목

    @Column(columnDefinition = "TEXT")
    private String description; // 기사 본문

    private String thumbnail; // 기사 썸네일 이미지 주소

    @Column(nullable = false)
    private String publisher; // 기사 언론사

    @Column(nullable = false)
    private String link; // 기사 링크

    @Column(nullable = false)
    private LocalDateTime publishedAt; // 작성일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NewsType newsType; // 뉴스 타입

    private String stockCode; // 뉴스가 속하는 주식 코드 (종목 뉴스인 경우)

    @Builder
    public News(String title, String summary, String description, String thumbnail, String publisher, String link, LocalDateTime publishedAt, NewsType newsType, String stockCode) {
        this.title = title;
        this.summary = summary;
        this.description = description;
        this.thumbnail = thumbnail;
        this.publisher = publisher;
        this.link = link;
        this.publishedAt = publishedAt;
        this.newsType = newsType;
        this.stockCode = stockCode;
    }

}
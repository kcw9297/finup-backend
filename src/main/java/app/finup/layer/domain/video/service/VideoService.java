package app.finup.layer.domain.video.service;

import app.finup.layer.domain.video.dto.VideoDto;

import java.util.List;

/**
 * YouTube API 영상 로직을 관리하는 인터페이스 (VideoLink는 수동으로 등록하는 경우)
 * @author kcw
 * @since 2025-12-07
 */

public interface VideoService {

    /**
     * 특정 영상의 상세 데이터 조회
     * @param videoUrl 영상 URL
     * @return 영상 상세 정보 DTO
     */
    VideoDto.Detail getDetail(String videoUrl);


    /**
     * 키워드 기반 영상 검색
     * @param keyword 영상 검색 키워드
     * @return 검색된 영상 목록 DTO 리스트 (상세정보 미포함)
     */
    List<VideoDto.Row> search(String keyword);


    /**
     * 홈페이지 유튜브 영상 추천 (Index 페이지)
     * RAG, VectorDB와 관여되있지 않고, AI가 추천한 검색어 기반으로만 검색
     * @return 추천 영상목록 DTO 리스트
     */
    List<VideoDto.Row> recommendForHome();


    /**
     * 주식 유튜브 영상 추천 (특정 상세종목)
     * @param stockId 주식 고유번호 (6자리)
     * @param stockName 주식 종목명 (한글)
     * @return 추천 영상목록 DTO 리스트
     */
    List<VideoDto.RecommendRow> recommendForStock(String stockId, String stockName);
}

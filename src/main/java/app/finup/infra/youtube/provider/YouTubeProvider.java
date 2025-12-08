package app.finup.infra.youtube.provider;

import app.finup.infra.youtube.dto.YouTube;

import java.util.List;

/**
 * YouTube Data API 제공 인터페이스
 * 무료 기준 1일 1만건(quota) 제한
 * @author kcw
 * @since 2025-12-05
 */
public interface YouTubeProvider {

    /**
     * 유튜브 영상 정보 조회 - ID 기반 (11자리)
     * 조회 시 quota 1개 소모
     * @param videoId 유튜브 영상 고유번호(아이디) - 11자리
     * @return 유튜브 영상 정보
     */
    YouTube.Detail getVideo(String videoId);


    /**
     * 유튜브 영상 정보 조회 - ID 기반 (11자리)
     * 검색 시 quota 101개 소모
     * @param q 유튜브 영상 검색어 (keyword)
     * @return 검색된 유튜브 영상 데이터 목록
     */
    List<YouTube.Row> searchVideo(String q);

}

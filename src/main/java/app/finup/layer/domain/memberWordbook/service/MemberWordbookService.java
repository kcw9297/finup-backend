package app.finup.layer.domain.memberWordbook.service;

import app.finup.layer.domain.memberWordbook.dto.MemberWordbookDto;

import java.util.List;

/**
 * 회원 단어장 비즈니스 로직 인터페이스
 * @author khj
 * @since 2025-12-14
 */
public interface MemberWordbookService {

    /**
     * 내 단어장 목록 조회
     */
    List<MemberWordbookDto.Row> getMyWordbook();

    /**
     * 단어장에 단어 추가
     */
    void add(MemberWordbookDto.Add rq);

    /**
     * 단어장에서 단어 삭제
     */
    void remove(Long termId);


    /**
     * 단어 스크랩 여부 판단
     */

    boolean isAdded(Long termId);

    /**
     * 단어 암기 상태 변경
     *
     * @param termId 대상 금융 용어 ID
     * @param rq 암기 상태 변경 요청 DTO
     * @return 변경된 암기 상태 정보
     */
    MemberWordbookDto.MemorizedWord memorize(Long termId, MemberWordbookDto.Memorize rq);
}

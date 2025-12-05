package app.finup.layer.domain.member.service;


import app.finup.common.dto.Page;
import app.finup.layer.domain.member.dto.MemberDto;

import java.util.List;

/**
 * 회원 관련 비즈니스 로직 인터페이스
 * @author khj
 * @since 2025-12-04
 */
public interface MemberService {
    /**
     * 회원 정보 검색
     * @param rq 검색 요청 DTO
     * @return 페이징된 DTO 리스트
     */
    Page<MemberDto.Row> search(MemberDto.Search rq);
}

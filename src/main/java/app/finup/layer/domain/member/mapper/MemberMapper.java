package app.finup.layer.domain.member.mapper;

import app.finup.layer.domain.member.dto.MemberDto;

import java.util.List;

public interface MemberMapper {
    List<MemberDto.Row> search(MemberDto.Search rq);
    Long countForSearch(MemberDto.Search rq);
}

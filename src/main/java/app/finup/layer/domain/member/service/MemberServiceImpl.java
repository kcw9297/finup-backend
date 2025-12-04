package app.finup.layer.domain.member.service;

import app.finup.common.dto.Page;
import app.finup.layer.domain.member.dto.MemberDto;
import app.finup.layer.domain.member.dto.MemberDtoMapper;
import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public Page<MemberDto.Row> search(MemberDto.Search rq) {
        List<MemberDto.Row> rp = memberRepository.findAll(Sort.by(Sort.Direction.DESC, "memberId"))
                .stream()
                .map(MemberDtoMapper::toRow)
                .toList();
        Long count = memberRepository.count();

        return Page.of(rp, count.intValue(), rq.getPageNum(), rq.getPageSize());
    }
}

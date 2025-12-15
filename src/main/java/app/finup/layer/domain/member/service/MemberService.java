package app.finup.layer.domain.member.service;


import app.finup.common.dto.Page;
import app.finup.layer.domain.member.dto.MemberDto;
import org.springframework.web.multipart.MultipartFile;


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


    /**
     * 회원 정보 전체 리스트(Pdf 다운로드 전용)
     * @return 회원 리스트
     */
    List<MemberDto.Row> getMemberList();

    /**
     * 회원가입
     *
     * @return 가입한 회원 정보
     */
    MemberDto.Row join(MemberDto.Join rq);

    MemberDto.Row getDetail(Long memberId);
    /**
     * 회원 닉네임 수정
     * @param
     * @param rq 닉네임 수정 요청 DTO
     */
    void editNickname(MemberDto.EditNickname rq);

    /**
     * 회원 비밀번호 수정
     * @param
     * @param rq 비밀번호 수정 요청 DTO
     */
    void editPassword(MemberDto.EditPassword rq);

    /**
     * 회원 프로필 이미지 수정
     * @param memberId 회원 번호
     * @param file 업로드 이미지 파일
     */
    void editProfileImage(Long memberId, MultipartFile file);

}

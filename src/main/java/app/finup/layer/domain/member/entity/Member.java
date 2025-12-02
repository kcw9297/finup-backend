package app.finup.layer.domain.member.entity;

import app.finup.layer.base.entity.BaseEntity;
import app.finup.layer.domain.member.enums.MemberRole;
import app.finup.layer.domain.member.enums.MemberSocial;
import app.finup.layer.domain.uploadfile.entity.UploadFile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(
        name = "member",
        uniqueConstraints = { // 복합 UK (소셜유형, 소셜회원번호) 정보는 고유 값
                @UniqueConstraint(columnNames = {"social", "social_id"})
        }
)
@DynamicUpdate
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long memberId;

    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private MemberSocial social;

    @Column(updatable = false)
    private String socialId;

    @OneToOne(
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, // 이미지 자동 저장/수정/삭제 처리
            orphanRemoval = true // null 설정 시 이미지 엔티티 자동 삭제 처리
    )
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "profile_image_id")
    private UploadFile profileImage;

    // 생성자
    @Builder
    private Member(String email, String password, String nickname, MemberSocial social, String socialId, MemberRole role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.social = social;
        this.socialId = socialId;
        this.role = role;
        setDefault();
    }

    // 생성 시 기본 설정
    private void setDefault() {
        this.isActive = true;
    }

    /**
     * 일반 회원가입을 위한 엔티티 생성
     * @param email 이메일
     * @param password 비밀번호
     * @param nickname 닉네임
     * @return 일반회원 엔티티
     */
    public static Member joinNormal(String email, String password, String nickname) {

        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .social(MemberSocial.NORMAL)
                .role(MemberRole.NORMAL)
                .build();
    }

    /**
     * 소셜 회원가입을 위한 엔티티 생성
     * @param email 이메일
     * @param nickname 닉네임 (현재는 서비스에서 임의로 생성하여 제공)
     * @param social 소셜 유형 (GOOGLE, NAVER, ...)
     * @param socialId 소셜 회원번호
     * @return 소셜회원 엔티티
     */
    public static Member joinSocial(String email, String nickname, MemberSocial social, String socialId) {

        return Member.builder()
                .email(email)
                .nickname(nickname)
                .social(social)
                .socialId(socialId)
                .role(MemberRole.NORMAL)
                .build();
    }

    /**
     * 관리자 계정 엔티티 생성
     * @param email 이메일
     * @param password 비밀번호
     * @param nickname 닉네임
     * @return 관리자 회원 엔티티
     */
    public static Member createAdmin(String email, String password, String nickname) {

        return Member.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .social(MemberSocial.NORMAL)
                .role(MemberRole.ADMIN)
                .build();
    }
}
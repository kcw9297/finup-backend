package app.finup.layer.base.runner;


import app.finup.common.constant.Env;
import app.finup.common.utils.EnvUtils;
import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.member.repository.MemberRepository;
import app.finup.layer.domain.reboard.entity.Reboard;
import app.finup.layer.domain.reboard.repository.ReboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 애플리케이션 시작 후 로컬 환경에서 테스트 데이터 삽입 처리 클래스
 * @author kcw
 * @since 2025-11-26
 */

@Component
@Transactional
@RequiredArgsConstructor
public class InsertDefaultDataRunner implements ApplicationRunner {

    private final Random random = new Random();
    private final Environment env;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final ReboardRepository reboardRepository;

    private static final List<String> RANDOM_NAMES = List.of(
            "김민준", "이서연", "박지후", "최지민", "정하늘",
                "한예린", "윤서준", "장도윤", "임하은", "조현우",
                "서지호", "강민지", "배도현", "문유진", "오채원",
                "신은호", "홍지아", "권하윤", "남도영", "백지후"
    );

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // [1] 삽입 전 현재 환경설정 확인
        boolean isCreate = EnvUtils.isPropertyEquals(env, Env.PROP_DDL_AUTO, Env.DDL_AUTO_CREATE);

        // [2] ddl-auto 옵션이 create 인 경우에만 생성
        if (isCreate) {
            createTestMembers();
            createTestReboards();
        }
    }

    // 테스트 회원 생성
    private void createTestMembers() {

        // 일반 회원
        List<Member> members = IntStream.range(0, RANDOM_NAMES.size())
                .mapToObj(i -> Member.joinNormal("test%03d@t.t".formatted(i+1), passwordEncoder.encode("test"), RANDOM_NAMES.get(i)))
                .collect(Collectors.toList());

        // 관리자 회원
        members.add(Member.createAdmin("admin@a.a", passwordEncoder.encode("admin"), "관리자"));

        // 저장
        memberRepository.saveAll(members);
    }

    // 테스트 Reboard 생성
    private void createTestReboards() {

        List<Reboard> reboards = IntStream.rangeClosed(1, 100)
                .mapToObj(i -> new Reboard("작성자%03d".formatted(i), "제목%03d".formatted(i), "본문%03d".formatted(i)))
                .collect(Collectors.toList());

        reboardRepository.saveAll(reboards);
    }


}

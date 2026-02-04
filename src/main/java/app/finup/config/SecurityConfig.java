package app.finup.config;

import app.finup.common.constant.Url;
import app.finup.common.utils.EnvUtils;
import app.finup.infra.file.storage.FileStorage;
import app.finup.layer.domain.member.enums.MemberRole;
import app.finup.layer.domain.member.repository.MemberRepository;
import app.finup.security.filter.CsrfVerificationFilter;
import app.finup.security.filter.JwtAuthenticationFilter;
import app.finup.security.handler.CustomAccessDeniedHandler;
import app.finup.security.handler.CustomAuthenticationEntryPoint;
import app.finup.security.service.NormalUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security Config 클래스
 * @author kcw
 * @since 2025-11-26
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // 사용 의존성
    private final Environment env;
    private final MemberRepository memberRepository;
    private final FileStorage fileStorage;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CsrfVerificationFilter csrfVerificationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    // 사용 상수
    @Value("${app.origin}")
    private String origin;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // [1] Spring Security 기본 설정
        HttpSecurity config = http

                // ▶ CSRF 설정 - 만약 Cookie에 AccessToken을 담는 경우 설정 권장 (방어 강화)
                //.csrf(customer -> customer
                //        .ignoringRequestMatchers(
                //                Url.LOGIN, Url.PATTERN_AUTH, Url.PATTERN_OAUTH, Url.LOGOUT, Url.PATTERN_PUBLIC,
                //                "/", "/index.html", "/favicon.ico", "/assets/**"
//
                //        ) // 일반&소셜 로그인, 로그아웃
                //        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                //        .csrfTokenRepository(csrfTokenRepository()) // JS 접근이 가능한 'XSRF-TOKEN' 값을 포함한 쿠키를 전달함
                //)
                .csrf(AbstractHttpConfigurer::disable)

                // ▶ 필터 설정 - 커스텀 필터 추가
                // UsernamePasswordAuthenticationFilter 는 세션기반 인증 시에만 동작)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                //.addFilterAfter(csrfVerificationFilter, CsrfFilter.class) // 요청 시 토큰 값 자동 생성


                // ▶ URL 접근 통제
                .authorizeHttpRequests(customer -> customer

                        // 익명 사용자만 허용 (비회원)
                        .requestMatchers(Url.LOGIN).anonymous() // 로그인

                        // 모두 허용
                        //.requestMatchers("/", "/index.html", "/favicon.ico", "/assets/**").permitAll()
                        .requestMatchers(Url.PATTERN_PUBLIC).permitAll() // 공용 API
                        .requestMatchers(Url.PATTERN_AUTH).permitAll() // 인증 API
                        .requestMatchers(Url.LOGOUT).permitAll() // 로그아웃

                        // 관리자만 허용
                        .requestMatchers(Url.PATTERN_ADMIN).hasRole(MemberRole.ADMIN.name())

                        // 그 외 API 요청은 인증된 사용자만 허용 (/api 로 시작하는 경우)
                        .requestMatchers(Url.PATTERN_API).authenticated()

                        // 그 외의 요청들은 모두 허용
                        .anyRequest().permitAll()
                )

                // ▶ 세션 설정 - 미사용 (JWT 기반 인증)
                .sessionManagement(customer -> customer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ▶ 예외 처리 - 로그인 실패, 권한 부족 처리 클래스 연결
                .exceptionHandling(customer -> customer
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                );


        // [2] 프로필에 따른 추가 설정
        // ▶ CORS 설정 - @CrossOrigin 역할과 거의 동일 (Frontend 구분 시 필수 설정)
        config.cors(customer -> customer.configurationSource(corsConfigurationSource()));

        // [3] 설정 저장
        return http.build();
    }


    @Bean // CSRF 쿠키를 관리하는 Repository
    public CookieCsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieCustomizer(cookie -> {
            cookie.maxAge(86400);  // 24시간 (초 단위)
            cookie.sameSite("Lax"); // SameSite 속성
            cookie.secure(EnvUtils.isProd(env));
            cookie.path("/");
        });
        return repository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new NormalUserDetailService(memberRepository, fileStorage);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        // [1] CORS 설정
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(origin)); // 허용할 도메인 설정
        //config.setAllowedOrigins(List.of("http://localhost:5173"));
        //config.setAllowedMethods(List.of(("*"))); // 허용할 HTTP 메소드 설정
        config.setAllowedMethods(List.of("OPTIONS", "GET", "POST", "PUT", "DELETE", "PATCH")); // 허용 method
        config.setAllowedHeaders(List.of(("*")));   // 허용 Header
        config.setAllowCredentials(true);           // 인증 요청을 포함한 쿠키 정보 허용
        config.setMaxAge(3600L);
        //config.applyPermitDefaultValues();

        // [2] 설정을 적용할 경로 적용 후 반환
        source.registerCorsConfiguration("/**", config); // 모든 경로에 CORS 설정 등록
        return source;
    }

}

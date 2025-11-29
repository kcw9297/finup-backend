package app.finup.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import app.finup.common.utils.EnvUtils;


/**
 * WebMvcConfigurer 설정 클래스
 * @author kcw
 * @since 2025-11-26
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final Environment env;

    @Value("${file.handler}")
    private String fileHandler;

    @Value("${file.dir}")
    private String fileDir;

    @Value("${file.domain}")
    private String fileDomain;

    // 프로필 판별 값
    private boolean isLocal;
    private boolean isProd;
    private String allFilePath;

    // 빈 초기화 후 앱 시작 전 호출
    @PostConstruct
    private void init() {
        this.isLocal = EnvUtils.isLocal(env);
        this.isProd = EnvUtils.isProd(env);
        this.allFilePath = "%s/**".formatted(fileHandler);
    }

    // 사용 Interceptor 등록
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

    }

    // 사용 file handler 등록
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 로컬에서만 실제 파일 경로 매핑
        // 배포 환경에서는, S3 파일 경로를 직접 매핑
        if (isLocal)
            registry.addResourceHandler(allFilePath)
                    .addResourceLocations("file:/%s/".formatted(fileDir));
    }

}

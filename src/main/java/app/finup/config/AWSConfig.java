package app.finup.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * AWS 관련 빈 설정 클래스
 * @author kcw
 * @since 2025-12-18
 */

@Configuration(proxyBeanMethods = false)
public class AWSConfig {

    @Bean
    public S3Client s3Client() {

        return S3Client.builder()
                .region(Region.AP_NORTHEAST_2) // 지역 : 서울
                .credentialsProvider(DefaultCredentialsProvider.create()) // 로컬의 .aws/credentials 파일 혹은 EC2 내 ROLE 사용
                .build();
    }
}

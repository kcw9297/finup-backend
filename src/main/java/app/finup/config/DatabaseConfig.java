package app.finup.config;

import app.finup.layer.domain.member.dto.MemberDto;
import app.finup.layer.domain.notice.dto.NoticeDto;
import app.finup.layer.domain.reboard.dto.ReboardDto;
import app.finup.layer.domain.study.dto.StudyDto;
import app.finup.layer.domain.studyword.dto.StudyWordDto;
import app.finup.layer.domain.videolink.dto.VideoLinkDto;
import app.finup.layer.domain.words.dto.WordsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.sql.DataSource;

/**
 * Redis 제외 Database 관련 Config 클래스
 * @author kcw
 * @since 2025-11-26
 */
@Slf4j
@MapperScan(basePackages = "app.finup.layer.domain.*.mapper")
@Configuration
@EnableJpaAuditing
@RequiredArgsConstructor
public class DatabaseConfig {

    // 사용 DBCP
    private final DataSource dataSource;

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mybatis/**/*.xml"));

        // Configuration 설정
        org.apache.ibatis.session.Configuration configuration =
                new org.apache.ibatis.session.Configuration();

        configuration.setMapUnderscoreToCamelCase(true); // Underscore -> CamelCase 일부 매핑
        registerAliases(configuration);  // 커스텀 alias 등록
        sqlSessionFactoryBean.setConfiguration(configuration);
        return sqlSessionFactoryBean.getObject();
    }

    private void registerAliases(org.apache.ibatis.session.Configuration configuration) {
        TypeAliasRegistry registry = configuration.getTypeAliasRegistry();

        // 각 도메인의 DTO 패키지에서 매퍼에 사용할 내부클래스 등록
        registry.registerAlias("ReboardRow", ReboardDto.Row.class);
        registry.registerAlias("NoticeRow", NoticeDto.Row.class);
        registry.registerAlias("StudyRow", StudyDto.Row.class);
        registry.registerAlias("StudyWordRow", StudyWordDto.Row.class);
        registry.registerAlias("MemberRow", MemberDto.Row.class);
        registry.registerAlias("VideoLinkRow", VideoLinkDto.Row.class);
        registry.registerAlias("WordsRow", WordsDto.Row.class);
    }

}
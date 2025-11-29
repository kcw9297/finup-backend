package app.finup.common.manager;

import app.finup.common.utils.EnvUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * FileUrlProvider 구현체
 * @author kcw
 * @since 2025-11-29
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileUrlProviderImpl implements FileUrlProvider {

    private final Environment env;

    @Value("${app.domain}")
    private String appDomain;

    @Value("${server.port}")
    private Integer serverPort;

    @Value("${file.domain}")
    private String fileDomain;

    @Override
    public String getFullPath(String path) {
        return EnvUtils.isProd(env) ?
                "%s%s".formatted(fileDomain, path) :
                "http://%s:%s%s%s".formatted(appDomain, serverPort, fileDomain, path);
    }
}

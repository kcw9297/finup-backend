package app.finup.layer.domain.news.component;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;


@Component
public class NewsIdGenerator {
    public String generate(String link){
        return DigestUtils.sha256Hex(link);
    }
}

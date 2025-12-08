package app.finup.infra.youtube.provider;

import app.finup.infra.youtube.dto.YouTube;
import app.finup.infra.youtube.utils.YouTubeUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class V3YouTubeProviderTest {

    @Autowired
    private YouTubeProvider provider;


    @Test
    void getVideo() {
        YouTube.Detail video =
                provider.getVideo(YouTubeUtils.parseVideoId("https://www.youtube.com/watch?v=4Mq_onxIOZQ"));

        log.warn("video = {}", video);
    }

}
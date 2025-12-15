package app.finup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(
        exclude = {
                org.springframework.ai.vectorstore.chroma.autoconfigure.ChromaVectorStoreAutoConfiguration.class
        }
)
public class FinupApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinupApplication.class, args);
	}

}

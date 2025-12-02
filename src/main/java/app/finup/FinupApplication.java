package app.finup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FinupApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinupApplication.class, args);
	}

}

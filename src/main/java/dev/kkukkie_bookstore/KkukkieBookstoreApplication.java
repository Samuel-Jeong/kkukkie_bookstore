package dev.kkukkie_bookstore;

import dev.kkukkie_bookstore.config.KakaoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(KakaoConfig.class)
public class KkukkieBookstoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(KkukkieBookstoreApplication.class, args);
	}

}

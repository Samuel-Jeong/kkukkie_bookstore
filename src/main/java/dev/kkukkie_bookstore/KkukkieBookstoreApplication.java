package dev.kkukkie_bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class KkukkieBookstoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(KkukkieBookstoreApplication.class, args);
	}

}

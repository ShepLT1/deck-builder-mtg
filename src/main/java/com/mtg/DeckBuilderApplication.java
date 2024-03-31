package com.mtg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DeckBuilderApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(DeckBuilderApplication.class);
		application.run(args);
	}

}

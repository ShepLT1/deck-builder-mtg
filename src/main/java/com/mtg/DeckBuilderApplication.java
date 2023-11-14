package com.mtg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DeckBuilderApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(DeckBuilderApplication.class);
		application.run(args);
	}

}

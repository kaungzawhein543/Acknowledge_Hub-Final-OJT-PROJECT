package com.ace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AcKnowledgeHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcKnowledgeHubApplication.class, args);
	}

}

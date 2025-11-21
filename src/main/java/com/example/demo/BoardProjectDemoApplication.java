package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing; // Auditing 활성화 어노테이션 임포트

@EnableJpaAuditing // JPA의 Auditing 기능을 애플리케이션 전체에 활성화
@SpringBootApplication
public class BoardProjectDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoardProjectDemoApplication.class, args);
	}

}

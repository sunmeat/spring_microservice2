package com.sunmeat.halloffame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // без этой аннотации сервис-фейкомётчик отзывы отправлять не будет
@SpringBootApplication
public class HallOfFameApplication {

	public static void main(String[] args) {
		SpringApplication.run(HallOfFameApplication.class, args);
	}

}
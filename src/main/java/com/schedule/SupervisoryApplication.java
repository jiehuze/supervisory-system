package com.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SupervisoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupervisoryApplication.class, args);
	}

}

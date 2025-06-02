package com.ayuntamiento.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AyuntamientoServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AyuntamientoServiceApplication.class, args);
	}

}

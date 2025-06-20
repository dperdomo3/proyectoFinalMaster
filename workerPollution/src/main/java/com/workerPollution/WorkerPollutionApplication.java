package com.workerPollution;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WorkerPollutionApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkerPollutionApplication.class, args);
	}
}

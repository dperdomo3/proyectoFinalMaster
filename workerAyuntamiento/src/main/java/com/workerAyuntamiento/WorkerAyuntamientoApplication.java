package com.workerAyuntamiento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WorkerAyuntamientoApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkerAyuntamientoApplication.class, args);
	}

}

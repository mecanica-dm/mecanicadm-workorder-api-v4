package com.mecanicadm.workorder_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class WorkOrderApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkOrderApiApplication.class, args);
	}

}

package com.mecanicadm.workorder_api;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class WorkOrderApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(WorkOrderApiApplication.class, args);
    }
}

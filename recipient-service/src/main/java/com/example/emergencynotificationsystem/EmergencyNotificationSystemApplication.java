package com.example.emergencynotificationsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class EmergencyNotificationSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmergencyNotificationSystemApplication.class, args);
	}

}

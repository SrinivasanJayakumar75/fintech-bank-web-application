package com.fintech.fintech.application;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import com.fintech.fintech.application.auth_users.entities.User;
import com.fintech.fintech.application.enums.NotificationType;
import com.fintech.fintech.application.notification.dtos.NotificationDto;
import com.fintech.fintech.application.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;


@SpringBootApplication
@EnableAsync
@RequiredArgsConstructor
public class FintechApplication {

	//private final NotificationService notificationService;

	public static void main(String[] args) {
		SpringApplication.run(FintechApplication.class, args);
	}

	// @Bean
	// CommandLineRunner runner(){
	// 	return args -> {
	// 		NotificationDto notificationDto = NotificationDto.builder()
	// 		.recipient("sjayakumar862412@gmail.com")
	// 		.subject("Hello testing email")
	// 		.body("Hey, this is a test email")
	// 		.type(NotificationType.EMAIL)
	// 		.build();

	// 		notificationService.sendEmail(notificationDto, new User());
	// 	};
	// }

}

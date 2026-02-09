package com.example.invitation_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {
		"com.example.invitation_service",
		"com.campuslink.common",
		"com.CampusLink",
		"com.campuslink"
})
@EnableFeignClients
//@EnableCaching
public class InvitationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvitationServiceApplication.class, args);
	}

}

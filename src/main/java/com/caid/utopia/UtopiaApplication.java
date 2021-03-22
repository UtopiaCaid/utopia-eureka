package com.caid.utopia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class UtopiaApplication {

	public static void main(String[] args) {
		SpringApplication.run(UtopiaApplication.class, args);
	}

}

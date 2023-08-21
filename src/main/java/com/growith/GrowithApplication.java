package com.growith;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GrowithApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrowithApplication.class, args);
	}

}

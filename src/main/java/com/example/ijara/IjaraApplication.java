package com.example.ijara;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IjaraApplication {

	public static void main(String[] args) {
		SpringApplication.run(IjaraApplication.class, args);
	}

}

package com.benica.catatcovid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class CatatcovidApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatatcovidApplication.class, args);
	}

}

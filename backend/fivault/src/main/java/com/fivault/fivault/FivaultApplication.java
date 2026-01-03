package com.fivault.fivault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class FivaultApplication {

	public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(FivaultApplication.class, args);
	}

}

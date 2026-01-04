package com.fivault.fivault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import com.fivault.fivault.util.RandomUtil;

@SpringBootApplication
public class FivaultApplication {

    public static void main(String[] args) {
//        GeneratePepper();
//        GenerateJWTSecret();
        ConfigurableApplicationContext context = SpringApplication.run(FivaultApplication.class, args);
    }

    public static void GeneratePepper() {
        String encodedPepper = RandomUtil.randomBase64(32);
        System.out.println("Generated pepper: " + encodedPepper);
    }

    public static void GenerateJWTSecret() {
        String jwtSecret = RandomUtil.randomBase64Url(32);
        System.out.println("Generated JWT secret: " + jwtSecret);
    }

}

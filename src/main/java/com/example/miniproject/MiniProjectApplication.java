package com.example.miniproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.example.miniproject.config.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class MiniProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(MiniProjectApplication.class, args);
    }
}

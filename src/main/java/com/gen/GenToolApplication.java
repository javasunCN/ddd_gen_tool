package com.gen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class GenToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(GenToolApplication.class, args);
    }

}

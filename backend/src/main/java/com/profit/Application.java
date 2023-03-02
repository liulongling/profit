package com.profit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@PropertySource(value = {
        "classpath:/base.properties"
}, encoding = "UTF-8", ignoreResourceNotFound = true)
public class Application{
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

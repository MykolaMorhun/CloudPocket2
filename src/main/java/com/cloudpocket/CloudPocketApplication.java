package com.cloudpocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:properties/db.properties")
public class CloudPocketApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudPocketApplication.class, args);
	}

}

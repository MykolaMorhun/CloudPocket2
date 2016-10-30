package com.cloudpocket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
public class CloudPocketApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(CloudPocketApplication.class, args);
	}

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CloudPocketApplication.class);
    }

}

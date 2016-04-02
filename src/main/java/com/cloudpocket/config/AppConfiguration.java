package com.cloudpocket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.sql.DataSource;

@Configuration
@ComponentScan("com.cloudpocket")
public class AppConfiguration {

    @Bean
    public DataSource dataSource(
            @Value("${db.url}") String url,
            @Value("${db.username}") String username,
            @Value("${db.password}") String password) {
        DriverManagerDataSource source = new DriverManagerDataSource(url, username, password);
        source.setDriverClassName("com.mysql.jdbc.Driver");
        return source;
    }

}
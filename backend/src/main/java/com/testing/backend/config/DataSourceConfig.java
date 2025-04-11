package com.testing.Backend.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() {
        System.out.println("Creating data source with URL: " + maskSensitiveInfo(url));
        System.out.println("Username is set: " + (username != null && !username.isEmpty()));
        System.out.println("Password is set: " + (password != null && !password.isEmpty()));
        
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        
        // Configure connection pool
        dataSource.setMaximumPoolSize(3);
        dataSource.setMinimumIdle(1);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(10000);
        dataSource.setMaxLifetime(30000);
        
        return dataSource;
    }
    
    private String maskSensitiveInfo(String input) {
        if (input == null) return "null";
        
        // Mask username/password in database URLs
        return input.replaceAll("//([^:]+):([^@]+)@", "//*****:*****@");
    }
}
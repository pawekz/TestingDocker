package com.testing.backend.config;

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
        
        // Ensure we have a valid URL
        if (url == null || url.isEmpty() || url.contains("${SPRING_DATASOURCE_URL}")) {
            url = "jdbc:sqlserver://testingdockerserver.database.windows.net:1433;database=testingdockerdb;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30";
            System.out.println("URL was invalid, using default: " + maskSensitiveInfo(url));
        }
        
        // Check if URL starts with jdbc: prefix
        if (!url.startsWith("jdbc:")) {
            System.out.println("URL does not start with jdbc: prefix, adding it");
            url = "jdbc:sqlserver://" + url;
        }
        
        // Ensure URL is properly formatted for SQL Server
        if (url.startsWith("jdbc:sqlserver://") && !url.contains(";database=")) {
            System.out.println("URL is missing database parameter, adding it");
            url = url + ";database=testingdockerdb;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30";
        }
        
        System.out.println("Final JDBC URL: " + maskSensitiveInfo(url));
        
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
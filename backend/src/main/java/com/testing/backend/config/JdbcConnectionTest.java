package com.testing.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Configuration
public class JdbcConnectionTest {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public CommandLineRunner testJdbcConnection() {
        return args -> {
            System.out.println("=== TESTING DIRECT JDBC CONNECTION ===");
            System.out.println("URL: " + maskSensitiveInfo(url));
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
            
            try {
                // Load the JDBC driver
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                System.out.println("JDBC driver loaded successfully");
                
                // Try to establish a connection
                System.out.println("Attempting to connect to the database...");
                Connection connection = DriverManager.getConnection(url, username, password);
                System.out.println("Connection established successfully!");
                
                // Close the connection
                connection.close();
                System.out.println("Connection closed");
            } catch (ClassNotFoundException e) {
                System.err.println("Failed to load JDBC driver: " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("Failed to connect to the database: " + e.getMessage());
                System.err.println("SQL State: " + e.getSQLState());
                System.err.println("Error Code: " + e.getErrorCode());
                
                // Print the stack trace
                e.printStackTrace();
            }
            
            System.out.println("=== END OF JDBC CONNECTION TEST ===");
        };
    }
    
    private String maskSensitiveInfo(String input) {
        if (input == null) return "null";
        
        // Mask username/password in database URLs
        return input.replaceAll("//([^:]+):([^@]+)@", "//*****:*****@");
    }
}
package com.example.nice_homeworkTask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class of the app.
 * - @SpringBootApplication: turns on Spring Boot auto-config + component scan
 * in this package.
 * - main(): starts the app and the embedded web server (Tomcat by default).
 */

@SpringBootApplication
public class NiceHomeworkTaskApplication {
    public static void main(String[] args) {
        // Boot the Spring context and start the server
        SpringApplication.run(NiceHomeworkTaskApplication.class, args);
    }
}

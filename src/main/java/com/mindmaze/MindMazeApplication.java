package com.mindmaze;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot main application entrypoint for MindMaze.
 * Bootstraps the web and application service layers while keeping the core domain pure and decoupled.
 */
@SpringBootApplication
public class MindMazeApplication {

    public static void main(String[] args) {
        SpringApplication.run(MindMazeApplication.class, args);
    }
}

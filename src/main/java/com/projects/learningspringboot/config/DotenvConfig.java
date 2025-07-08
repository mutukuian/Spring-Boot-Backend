package com.projects.learningspringboot.config;


import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {
    @PostConstruct
    public void loadDotenv() {
        Dotenv.configure()
                .ignoreIfMissing()
                .load();
    }
}


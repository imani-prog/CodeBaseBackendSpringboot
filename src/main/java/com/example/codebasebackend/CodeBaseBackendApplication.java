package com.example.codebasebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.sql.Connection;


@SpringBootApplication
@EnableScheduling
@EnableJpaAuditing
@EntityScan(basePackages = "com.example.codebasebackend.Entities")
@EnableJpaRepositories(basePackages = "com.example.codebasebackend.repositories")
public class CodeBaseBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CodeBaseBackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner testConnection(DataSource dataSource) {
        return args -> {
            try (Connection conn = dataSource.getConnection()) {
                System.out.println("CONNECTION SUCCESSFUL!");
                System.out.println("Catalog: " + conn.getCatalog());
            } catch (Exception e) {
                System.out.println("CONNECTION FAILED!");
                e.printStackTrace();
            }
        };
    }

}

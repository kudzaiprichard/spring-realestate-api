package com.intela.realestatebackend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest
public class BaseTestContainerTest {

    private static MySQLContainer<?> mysqlContainer;

    @BeforeAll
    public static void setUp() {
        if (!isMySQLRunning()) {
            // Start a fallback MySQL container if the existing one is not available
            mysqlContainer = new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("realestate")
                    .withUsername("root")
                    .withPassword("");
            mysqlContainer.start();

            // Set system properties to use the Testcontainers MySQL
            System.setProperty("spring.datasource.url", mysqlContainer.getJdbcUrl());
            System.setProperty("spring.datasource.username", mysqlContainer.getUsername());
            System.setProperty("spring.datasource.password", mysqlContainer.getPassword());
        }
    }

    @AfterAll
    public static void tearDown(){
        if (mysqlContainer != null && mysqlContainer.isRunning()){
            mysqlContainer.stop();
        }
    }

    private static boolean isMySQLRunning() {
        // Check if the existing MySQL container is accepting connections
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://mysql_realestate:3306/realestate", "root", "")) {
            return connection.isValid(1); // 1-second timeout to check connection validity
        } catch (SQLException e) {
            System.out.println("MySQL container is not running or not accepting connections.");
            return false;
        }
    }

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Test
    void test() {
        System.out.println("Tests loaded");
    }
}

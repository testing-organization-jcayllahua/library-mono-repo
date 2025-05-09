package com.test.msexampleusage.controller;

import com.test.libraryexample.MyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(ExampleController.class)
@Import(ExampleControllerIntegrationTest.TestConfig.class)
public class ExampleControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testExampleEndpoint() {
        // Act & Assert
        webTestClient.get()
                .uri("/example")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Test message from integration test");
    }

    @Configuration
    static class TestConfig {
        @Bean
        public MyService myService() {
            return new MyService() {
                @Override
                public String getMessage() {
                    return "Test message from integration test";
                }
            };
        }
    }
}

package com.test.msexampleusage.controller;

import com.test.libraryexample.MyService;
import com.test.msexampleusage.interfaces.rest.controller.ExampleController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.Mockito.when;

@WebFluxTest(ExampleController.class)
public class ExampleControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private MyService myService;

    @Test
    void testExampleEndpoint() {
        // Arrange
        when(myService.getMessage()).thenReturn("Test message from integration test");

        // Act & Assert
        webTestClient.get()
                .uri("/example")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Test message from integration test");
    }
}

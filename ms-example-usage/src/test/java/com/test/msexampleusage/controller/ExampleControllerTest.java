package com.test.msexampleusage.controller;

import com.test.libraryexample.MyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class ExampleControllerTest {

    @Mock
    private MyService myService;

    @InjectMocks
    private ExampleController exampleController;

    @Test
    void testExample() {
        // Arrange
        String expectedMessage = "Test message";
        Mockito.when(myService.getMessage()).thenReturn(expectedMessage);

        // Act
        Mono<String> result = exampleController.example();

        // Assert
        StepVerifier.create(result)
                .expectNext(expectedMessage)
                .verifyComplete();

        Mockito.verify(myService).getMessage();
    }
}
package com.test.msexampleusage.interfaces.rest.controller;

import com.test.libraryexample.MyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ExampleController {

    private final MyService myService;

    public ExampleController(MyService myService) {
        this.myService = myService;
    }

    @GetMapping("/example")
    public Mono<String> example() {
        return Mono.just(myService.getMessage());
    }
}

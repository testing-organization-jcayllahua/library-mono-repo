package com.test.msexampleusage.domain.repository;

import com.test.msexampleusage.domain.model.entities.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CustomerRepository {
    Mono<Customer> save(Customer customer);
    Mono<Customer> update(Customer customer);
    Mono<Void> deleteById(UUID id);

    Mono<Customer> findById(UUID id);
    Flux<Customer> findAll();
    Flux<Customer> findByLastName(String lastName);
    Mono<Boolean> existsById(UUID id);
    Mono<Boolean> existsByEmail(String email);
}

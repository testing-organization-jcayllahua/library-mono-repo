package com.test.msexampleusage.infrastructure.persistence.r2dbc;

import com.test.msexampleusage.infrastructure.persistence.entities.CustomerEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface R2dbcCustomerRepository extends ReactiveCrudRepository<CustomerEntity, UUID> {

    Mono<Boolean> existsByEmail(String email);

    Flux<CustomerEntity> findByLastName(String lastName);
}

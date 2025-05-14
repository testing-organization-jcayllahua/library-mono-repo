package com.test.msexampleusage.infrastructure.persistence.r2dbc.impl;

import com.test.msexampleusage.domain.exception.CustomerNotFoundException;
import com.test.msexampleusage.domain.model.entities.Customer;
import com.test.msexampleusage.domain.repository.CustomerRepository;
import com.test.msexampleusage.infrastructure.mapper.CustomerMapper;
import com.test.msexampleusage.infrastructure.persistence.entities.CustomerEntity;
import com.test.msexampleusage.infrastructure.persistence.r2dbc.R2dbcCustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerRepositoryImpl implements CustomerRepository {

    private final R2dbcCustomerRepository r2dbcCustomerRepository;
    private final CustomerMapper mapper;

    @Override
    public Mono<Customer> save(Customer customer) {
        log.debug("Saving customer: {}", customer);
        CustomerEntity customerEntity = mapper.toEntity(customer);
        return r2dbcCustomerRepository.save(customerEntity)
                            .map(mapper::fromEntity)
                            .doOnSuccess(c -> log.debug("Saved customer: {}", c))
                            .doOnError(e -> log.error("Error saving customer: {}", e.getMessage(), e));
    }

    @Override
    public Mono<Customer> update(Customer customer) {
        log.debug("Updating customer: {}", customer);
        if (customer.getId() == null) {
            return Mono.error(new IllegalArgumentException("Cannot update customer with null ID"));
        }
        return r2dbcCustomerRepository.existsById(customer.getId())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new CustomerNotFoundException("Customer with ID " + customer.getId() + " not found"));
                    }

                    // Ensure updatedAt is refreshed
                    Customer customerToUpdate = Customer.builder()
                            .id(customer.getId())
                            .firstName(customer.getFirstName())
                            .lastName(customer.getLastName())
                            .email(customer.getEmail())
                            .phoneNumber(customer.getPhoneNumber())
                            .createdAt(customer.getCreatedAt())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    CustomerEntity customerEntity = mapper.toEntity(customerToUpdate, false);

                    return r2dbcCustomerRepository.save(customerEntity);
                })
                .map(mapper::fromEntity)
                .doOnSuccess(c -> log.debug("Updated customer: {}", c))
                .doOnError(e -> log.error("Error updating customer: {}", e.getMessage(), e));
    }


    @Override
    public Mono<Void> deleteById(UUID id) {
        log.debug("Deleting customer by ID: {}", id);
        return r2dbcCustomerRepository.deleteById(id)
                .doOnSuccess(aVoid -> log.debug("Deleted customer by ID: {}", id))
                .doOnError(e -> log.error("Error deleting customer by ID: {}", e.getMessage(), e))
                ;
    }

    @Override
    public Mono<Customer> findById(UUID id) {
        log.debug("Finding customer by ID: {}", id);
        return r2dbcCustomerRepository.findById(id)
                .map(mapper::fromEntity)
                .doOnSuccess(c -> log.debug("Found customer: {}", c))
                .doOnError(e -> log.error("Error finding customer: {}", e.getMessage(), e))
                ;
    }

    @Override
    public Flux<Customer> findAll() {
        log.debug("Finding all customers");
        return r2dbcCustomerRepository.findAll()
                .map(mapper::fromEntity)
                .doOnComplete(() -> log.debug("All customers retrieved"));
    }

    @Override
    public Flux<Customer> findByLastName(String lastName) {
        log.debug("Finding customers by last name: {}", lastName);
        return r2dbcCustomerRepository.findByLastName(lastName)
                .map(mapper::fromEntity)
                .doOnComplete(() -> log.debug("Customers with last name {} retrieved", lastName))
                .doOnError(e -> log.error("Error finding customers by last name: {}", e.getMessage(), e))
                ;
    }

    @Override
    public Mono<Boolean> existsById(UUID id) {
        log.debug("Checking if customer exists by ID: {}", id);
        return r2dbcCustomerRepository.existsById(id)
                .doOnSuccess(exists -> log.debug("Customer exists by ID {}: {}", id, exists))
                .doOnError(e -> log.error("Error checking if customer exists by ID: {}", e.getMessage(), e))
                ;
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return r2dbcCustomerRepository.existsByEmail(email)
                .doOnSuccess(exists -> log.debug("Customer exists by email {}: {}", email, exists))
                .doOnError(e -> log.error("Error checking if customer exists by email: {}", e.getMessage(), e))
                ;
    }
}

package com.test.msexampleusage.application.service.commands;

import com.test.msexampleusage.domain.exception.CustomerNotFoundException;
import com.test.msexampleusage.domain.model.commands.CreateCustomerCommand;
import com.test.msexampleusage.domain.model.commands.DeleteCustomerCommand;
import com.test.msexampleusage.domain.model.commands.UpdateCustomerCommand;
import com.test.msexampleusage.domain.model.entities.Customer;
import com.test.msexampleusage.domain.repository.CustomerRepository;
import com.test.msexampleusage.domain.services.commands.CustomerCommandService;
import com.test.msexampleusage.infrastructure.validator.CustomerValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerCommandServiceImpl implements CustomerCommandService {

    private final CustomerRepository customerRepository;
    private final CustomerValidator validator;

    @Override
    public Mono<Customer> handle(CreateCustomerCommand command) {
        log.debug("Handling CreateCustomerCommand: {}", command);
        Customer customer = Customer.builder()
                .id(command.id())
                .firstName(command.firstName())
                .lastName(command.lastName())
                .email(command.email())
                .phoneNumber(command.phoneNumber())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return validator.validateNewCustomer(customer)
                .flatMap(customerRepository::save)
                .doOnSuccess(c -> log.debug("Customer created successfully with ID: {}", c.getId()))
                .doOnError(e -> log.error("Error creating customer: {}", e.getMessage(), e))
                ;
    }

    @Override
    public Mono<Customer> handle(UpdateCustomerCommand command) {
        log.debug("Handling UpdateCustomerCommand for ID: {}", command.customerId());

        return customerRepository.findById(command.customerId())
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Customer not found with ID: " + command.customerId())))
                .flatMap(existingCustomer -> {
                    Customer updatedCustomer = Customer.builder()
                            .id(existingCustomer.getId())
                            .firstName(command.firstName())
                            .lastName(command.lastName())
                            .email(command.email())
                            .phoneNumber(command.phoneNumber())
                            .createdAt(existingCustomer.getCreatedAt())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    return validator.validateUpdateCustomer(updatedCustomer)
                            .flatMap(customerRepository::update);
                })
                .doOnSuccess(c -> log.info("Customer updated successfully with ID: {}", command.customerId()))
                .doOnError(e -> log.error("Error updating customer with ID {}: {}", command.customerId(), e.getMessage()));
    }

    @Transactional
    @Override
    public Mono<Void> handle(DeleteCustomerCommand command) {
        log.debug("Handling DeleteCustomerCommand for ID: {}", command.customerId());

        return customerRepository.existsById(command.customerId())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new CustomerNotFoundException("Customer not found with ID: " + command.customerId()));
                    }
                    return customerRepository.deleteById(command.customerId())
                            .doOnSuccess(v -> log.info("Customer deleted successfully with ID: {}", command.customerId()));
                });
    }
}

package com.test.msexampleusage.application.service.queries;

import com.test.msexampleusage.domain.exception.CustomerNotFoundException;
import com.test.msexampleusage.domain.model.entities.Customer;
import com.test.msexampleusage.domain.model.queries.GetAllCustomersQuery;
import com.test.msexampleusage.domain.model.queries.GetCustomerQuery;
import com.test.msexampleusage.domain.repository.CustomerRepository;
import com.test.msexampleusage.domain.services.queries.CustomerQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerQueryServiceImpl implements CustomerQueryService {

    private final CustomerRepository customerRepository;

    @Override
    public Mono<Customer> handle(GetCustomerQuery query) {
        log.debug("Handling GetCustomerQuery for ID: {}", query.customerId());
        return customerRepository.findById(query.customerId())
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Customer not found with ID: " + query.customerId())))
                .doOnSuccess(customer -> log.debug("Retrieved customer: {}", customer))
                .doOnError(e -> log.error("Error fetching customer with ID {}: {}", query.customerId(), e.getMessage()));

    }

    @Override
    public Flux<Customer> handle(GetAllCustomersQuery query) {
        log.debug("Handling GetAllCustomersQuery");

        return customerRepository.findAll()
                .doOnComplete(() -> log.debug("All customers fetched successfully"));

    }
}

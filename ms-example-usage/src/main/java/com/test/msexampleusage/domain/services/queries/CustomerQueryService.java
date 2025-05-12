package com.test.msexampleusage.domain.services.queries;

import com.test.msexampleusage.domain.model.entities.Customer;
import com.test.msexampleusage.domain.model.queries.GetAllCustomersQuery;
import com.test.msexampleusage.domain.model.queries.GetCustomerQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerQueryService {
    Mono<Customer> handle(GetCustomerQuery query);
    Flux<Customer> handle(GetAllCustomersQuery query);
}

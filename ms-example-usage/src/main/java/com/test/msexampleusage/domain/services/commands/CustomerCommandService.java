package com.test.msexampleusage.domain.services.commands;

import com.test.msexampleusage.domain.model.commands.CreateCustomerCommand;
import com.test.msexampleusage.domain.model.commands.DeleteCustomerCommand;
import com.test.msexampleusage.domain.model.commands.UpdateCustomerCommand;
import com.test.msexampleusage.domain.model.entities.Customer;
import reactor.core.publisher.Mono;

public interface CustomerCommandService {
    Mono<Customer> handle(CreateCustomerCommand command);
    Mono<Customer> handle(UpdateCustomerCommand command);
    Mono<Void> handle(DeleteCustomerCommand command);
}

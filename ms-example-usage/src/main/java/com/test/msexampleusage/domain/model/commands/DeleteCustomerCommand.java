package com.test.msexampleusage.domain.model.commands;

import java.util.UUID;

public record DeleteCustomerCommand(UUID customerId) {
}

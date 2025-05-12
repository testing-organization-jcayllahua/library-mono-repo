package com.test.msexampleusage.domain.model.commands;

import java.util.UUID;

public record UpdateCustomerCommand(
        UUID customerId,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {
}

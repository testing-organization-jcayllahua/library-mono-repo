package com.test.msexampleusage.domain.model.commands;

import java.util.UUID;

public record CreateCustomerCommand(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {
}

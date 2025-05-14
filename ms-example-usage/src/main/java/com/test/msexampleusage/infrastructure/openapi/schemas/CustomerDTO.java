package com.test.msexampleusage.infrastructure.openapi.schemas;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Customer data transfer object")
public record CustomerDTO(

        @Schema(description = "Unique identifier of the customer", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Customer's first name", example = "John")
        String firstName,

        @Schema(description = "Customer's last name", example = "Doe")
        String lastName,

        @Schema(description = "Customer's email address", example = "john.doe@example.com")
        String email,

        @Schema(description = "Customer's phone number", example = "+51925685987")
        String phoneNumber,

        @Schema(description = "Timestamp when the customer was created", example = "2025-05-10T12:30:45")
        LocalDateTime createdAt,

        @Schema(description = "Timestamp when the customer was last updated", example = "2025-05-11T09:15:22")
        LocalDateTime updatedAt

) {}


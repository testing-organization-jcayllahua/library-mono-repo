package com.test.msexampleusage.infrastructure.validator;

import com.test.msexampleusage.domain.exception.ValidationException;
import com.test.msexampleusage.domain.model.entities.Customer;
import com.test.msexampleusage.domain.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomerValidator {

    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");

    private final CustomerRepository customerRepository;

    public Mono<Customer> validateNewCustomer(Customer customer) {
        log.debug("Validating new customer: {}", customer);
        return validateRequiredFields(customer)
                .flatMap(this::validateEmailFormat)
                .flatMap(this::validatePhoneFormat)
                .flatMap(this::validateEmailUniqueness);
    }

    public Mono<Customer> validateUpdateCustomer(Customer customer) {
        log.debug("Validating customer update: {}", customer);
        return validateRequiredFields(customer)
                .flatMap(this::validateEmailFormat)
                .flatMap(this::validatePhoneFormat);
    }

    private Mono<Customer> validateRequiredFields(Customer customer) {
        if (customer == null) {
            return Mono.error(new ValidationException("Customer cannot be null"));
        }
        if (customer.getFirstName() == null || customer.getFirstName().trim().isEmpty()) {
            return Mono.error(new ValidationException("First name is required"));
        }
        if (customer.getLastName() == null || customer.getLastName().trim().isEmpty()) {
            return Mono.error(new ValidationException("Last name is required"));
        }
        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            return Mono.error(new ValidationException("Email is required"));
        }
        return Mono.just(customer);
    }

    private Mono<Customer> validateEmailFormat(Customer customer) {
        if (!EMAIL_PATTERN.matcher(customer.getEmail()).matches()) {
            return Mono.error(new ValidationException("Invalid email format"));
        }
        return Mono.just(customer);
    }

    private Mono<Customer> validatePhoneFormat(Customer customer) {
        if (customer.getPhoneNumber() != null && !customer.getPhoneNumber().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(customer.getPhoneNumber()).matches()) {
                return Mono.error(new ValidationException("Invalid phone number format"));
            }
        }
        return Mono.just(customer);
    }

    private Mono<Customer> validateEmailUniqueness(Customer customer) {
        return customerRepository.existsByEmail(customer.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new ValidationException("Email already exists"));
                    }
                    return Mono.just(customer);
                });
    }
}

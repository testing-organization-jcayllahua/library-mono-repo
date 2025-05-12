package com.test.msexampleusage.interfaces.rest.controller;

import com.test.msexampleusage.domain.model.commands.CreateCustomerCommand;
import com.test.msexampleusage.domain.model.commands.DeleteCustomerCommand;
import com.test.msexampleusage.domain.model.commands.UpdateCustomerCommand;
import com.test.msexampleusage.domain.model.queries.GetAllCustomersQuery;
import com.test.msexampleusage.domain.model.queries.GetCustomerQuery;
import com.test.msexampleusage.domain.services.commands.CustomerCommandService;
import com.test.msexampleusage.domain.services.queries.CustomerQueryService;
import com.test.msexampleusage.infrastructure.mapper.CustomerMapper;
import com.test.msexampleusage.infrastructure.openapi.schemas.CustomerDTO;
import com.test.msexampleusage.interfaces.rest.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
@Slf4j
@Tag(name = "Customer", description = "Customer management API")
public class CustomerController {

    private final CustomerCommandService commandService;
    private final CustomerMapper mapper;
    private final CustomerQueryService queryService;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get customer by ID", description = "Retrieve a customer by their unique ID")
    @ApiResponses( value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "Customer found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404",
                    description = "Customer not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),

    })
    public Mono<ApiResponse<CustomerDTO>> getCustomer(
            @Parameter(description = "ID of the customer to retrieve", required = true,
                    example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id
    ){
        GetCustomerQuery query = new GetCustomerQuery(id);
        return queryService.handle(query)
                .map(mapper::toDto)
                .map(dto -> ApiResponse.success(dto,"Customer retrieved successfully"))
                .doOnSuccess(res -> log.info("Customer retrieved: {}", id));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all customers", description = "Retrieves all customers")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customers retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CustomerDTO.class))))
    })
    public Flux<CustomerDTO> getAllCustomers() {
        log.info("Received request to get all customers");

        GetAllCustomersQuery query = new GetAllCustomersQuery();

        return queryService.handle(query)
                .map(mapper::toDto)
                .doOnComplete(() -> log.info("All customers retrieved successfully"));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new customer", description = "Creates a new customer with the provided details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Customer created successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public Mono<ApiResponse<CustomerDTO>> createCustomer(@RequestBody CustomerDTO customerDTO) {
        log.info("Received request to create a new customer");

        CreateCustomerCommand command = new CreateCustomerCommand(
                customerDTO.id(),
                customerDTO.firstName(),
                customerDTO.lastName(),
                customerDTO.email(),
                customerDTO.phoneNumber()
        );

        return commandService.handle(command)
                .map(mapper::toDto)
                .map(dto -> ApiResponse.success(dto, "Customer created successfully"))
                .doOnSuccess(response -> log.info("Customer created with ID: {}", response.getData().id()));
    }

    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update a customer", description = "Updates an existing customer with the provided details")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customer updated successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public Mono<ApiResponse<CustomerDTO>> updateCustomer(
            @Parameter(description = "ID of the customer to update", required = true)
            @PathVariable UUID id,
            @RequestBody CustomerDTO customerDTO) {
        log.info("Received request to update customer with ID: {}", id);

        UpdateCustomerCommand command = new UpdateCustomerCommand(
                id,
                customerDTO.firstName(),
                customerDTO.lastName(),
                customerDTO.email(),
                customerDTO.phoneNumber()
        );

        return commandService.handle(command)
                .map(mapper::toDto)
                .map(dto -> ApiResponse.success(dto, "Customer updated successfully"))
                .doOnSuccess(response -> log.info("Customer updated: {}", id));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete a customer", description = "Deletes a customer by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Customer deleted successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer not found",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public Mono<ApiResponse<Void>> deleteCustomer(
            @Parameter(description = "ID of the customer to delete", required = true)
            @PathVariable UUID id) {
        log.info("Received request to delete customer with ID: {}", id);

        DeleteCustomerCommand command = new DeleteCustomerCommand(id);

        return commandService.handle(command)
                .then(Mono.just(ApiResponse.<Void>success(null, "Customer deleted successfully")))
                .doOnSuccess(response -> log.info("Customer deleted: {}", id));
    }
}

package com.test.msexampleusage.interfaces.rest.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Schema(description = "Standardized API response wrapper")
public class ApiResponse<T> {

    @Schema(description = "Response status (success/error)")
    private String status;

    @Schema(description = "Response message")
    private String message;

    @Schema(description = "Response data payload")
    private T data;

    @Schema(description = "Error code (in case of errors)")
    private String errorCode;

    @Schema(description = "Timestamp of the response")
    private LocalDateTime timestamp;

    private ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.status = "success";
        response.data = data;
        response.message = message;
        return response;
    }

    public static <T> ApiResponse<T> error(String message, String errorCode) {
        ApiResponse<T> response = new ApiResponse<>();
        response.status = "error";
        response.message = message;
        response.errorCode = errorCode;
        return response;
    }

}

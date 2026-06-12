package com.sasac.platform.common.exception;

import lombok.Getter;

/**
 * Base business exception.
 * <p>
 * Thrown by service-layer code when a business rule is violated.
 * The {@link GlobalExceptionHandler} translates it into a 400 response.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(String message) {
        super(message);
        this.code = "BUSINESS_ERROR";
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
}

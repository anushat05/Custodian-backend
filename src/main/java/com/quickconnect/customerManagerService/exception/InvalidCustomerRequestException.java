package com.quickconnect.customerManagerService.exception;

import java.util.List;

public class InvalidCustomerRequestException extends RuntimeException {
    private final List<String> errors;

    public InvalidCustomerRequestException(List<String> errors) {
        super("Invalid customer request");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}


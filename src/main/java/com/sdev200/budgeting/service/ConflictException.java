package com.sdev200.budgeting.service;

public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}

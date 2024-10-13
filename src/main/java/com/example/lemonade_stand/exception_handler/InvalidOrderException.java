package com.example.lemonade_stand.exception_handler;

// Custom exception to handle invalid order scenarios
public class InvalidOrderException extends RuntimeException {
    public InvalidOrderException(String message) {
        super(message); // Pass the error message to the superclass (RuntimeException)
    }
}

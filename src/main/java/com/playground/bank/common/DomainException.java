package com.playground.bank.common;

/** A business rule was violated (e.g. insufficient funds). */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}

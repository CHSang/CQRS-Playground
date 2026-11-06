package com.playground.bank.common;

import java.util.UUID;

public class ConcurrencyException extends RuntimeException {

    public ConcurrencyException(UUID aggregateId, long expectedVersion) {
        super("Account " + aggregateId + " was modified concurrently (expected version " + expectedVersion + ")");
    }
}

package com.playground.bank.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Facts that happened to a bank account. They are the source of truth:
 * the write side stores them, and the read side builds its views from them.
 */
public sealed interface AccountEvent {

    UUID accountId();

    Instant occurredAt();

    record AccountOpened(UUID accountId, String owner, BigDecimal initialDeposit, Instant occurredAt)
            implements AccountEvent {
    }

    record MoneyDeposited(UUID accountId, BigDecimal amount, Instant occurredAt)
            implements AccountEvent {
    }

    record MoneyWithdrawn(UUID accountId, BigDecimal amount, Instant occurredAt)
            implements AccountEvent {
    }

    record AccountClosed(UUID accountId, Instant occurredAt)
            implements AccountEvent {
    }
}

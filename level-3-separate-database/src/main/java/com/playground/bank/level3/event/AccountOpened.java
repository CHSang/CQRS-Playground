package com.playground.bank.level3.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Tells the read side that something changed. Just a notification:
 * it is not stored, the write table stays the source of truth.
 */
public record AccountOpened(UUID accountId, String owner, BigDecimal initialDeposit, Instant openedAt) {
}

package com.playground.bank.level3.query;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** A row of the "account_summary" table in the read database. */
public record AccountSummary(UUID accountId, String owner, BigDecimal balance, Instant openedAt) {
}

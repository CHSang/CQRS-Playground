package com.playground.bank.level1.query;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** What the read API returns. A plain DTO, never the JPA entity. */
public record AccountView(UUID accountId, String owner, BigDecimal balance, Instant openedAt) {
}

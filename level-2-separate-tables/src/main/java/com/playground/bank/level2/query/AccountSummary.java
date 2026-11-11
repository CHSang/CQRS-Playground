package com.playground.bank.level2.query;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Read table, shaped for what the screen shows. Filled only by
 * AccountProjection; the read API returns it as is.
 */
@Entity
@Table(name = "account_summary")
public class AccountSummary {

    @Id
    private UUID accountId;

    @Column(nullable = false)
    private String owner;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    private Instant openedAt;

    protected AccountSummary() {
    }

    public AccountSummary(UUID accountId, String owner, BigDecimal balance, Instant openedAt) {
        this.accountId = accountId;
        this.owner = owner;
        this.balance = balance;
        this.openedAt = openedAt;
    }

    public UUID getAccountId() { return accountId; }
    public String getOwner() { return owner; }
    public BigDecimal getBalance() { return balance; }
    public Instant getOpenedAt() { return openedAt; }
}

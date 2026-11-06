package com.playground.bank.query.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Read model: one denormalized row per account, shaped for list/detail screens. */
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
    private String status;

    private int transactionCount;

    private Instant openedAt;

    private Instant lastUpdatedAt;

    protected AccountSummary() {
    }

    public AccountSummary(UUID accountId, String owner, BigDecimal balance, Instant openedAt) {
        this.accountId = accountId;
        this.owner = owner;
        this.balance = balance;
        this.status = "OPEN";
        this.openedAt = openedAt;
        this.lastUpdatedAt = openedAt;
    }

    public void applyMovement(BigDecimal delta, Instant at) {
        balance = balance.add(delta);
        transactionCount++;
        lastUpdatedAt = at;
    }

    public void markClosed(Instant at) {
        status = "CLOSED";
        lastUpdatedAt = at;
    }

    public UUID getAccountId() { return accountId; }
    public String getOwner() { return owner; }
    public BigDecimal getBalance() { return balance; }
    public String getStatus() { return status; }
    public int getTransactionCount() { return transactionCount; }
    public Instant getOpenedAt() { return openedAt; }
    public Instant getLastUpdatedAt() { return lastUpdatedAt; }
}

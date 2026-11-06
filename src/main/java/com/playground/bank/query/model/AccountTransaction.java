package com.playground.bank.query.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Read model: statement lines, with the running balance precomputed. */
@Entity
@Table(name = "account_transaction", indexes = @Index(columnList = "accountId"))
public class AccountTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID accountId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(nullable = false)
    private Instant occurredAt;

    protected AccountTransaction() {
    }

    public AccountTransaction(UUID accountId, String type, BigDecimal amount, BigDecimal balanceAfter,
                              Instant occurredAt) {
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.occurredAt = occurredAt;
    }

    public Long getId() { return id; }
    public UUID getAccountId() { return accountId; }
    public String getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public Instant getOccurredAt() { return occurredAt; }
}

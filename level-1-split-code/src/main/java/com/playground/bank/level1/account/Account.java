package com.playground.bank.level1.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * The one and only table in Level 1. The command side writes it;
 * the query side reads the same rows as DTOs.
 */
@Entity
@Table(name = "account")
public class Account {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String owner;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false)
    private Instant openedAt;

    protected Account() {
    }

    public Account(String owner, BigDecimal initialDeposit) {
        this.id = UUID.randomUUID();
        this.owner = owner;
        this.balance = initialDeposit;
        this.openedAt = Instant.now();
    }

    public UUID getId() { return id; }
}

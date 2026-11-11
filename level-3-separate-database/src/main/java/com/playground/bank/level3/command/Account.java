package com.playground.bank.level3.command;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Write table. Belongs to the command side only and holds just what
 * the write side needs. The query side never reads this table.
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

    protected Account() {
    }

    public Account(String owner, BigDecimal initialDeposit) {
        this.id = UUID.randomUUID();
        this.owner = owner;
        this.balance = initialDeposit;
    }

    public UUID getId() { return id; }
    public String getOwner() { return owner; }
    public BigDecimal getBalance() { return balance; }
}

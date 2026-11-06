package com.playground.bank.command.domain;

import com.playground.bank.common.DomainException;
import com.playground.bank.event.AccountEvent;
import com.playground.bank.event.AccountEvent.AccountClosed;
import com.playground.bank.event.AccountEvent.AccountOpened;
import com.playground.bank.event.AccountEvent.MoneyDeposited;
import com.playground.bank.event.AccountEvent.MoneyWithdrawn;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Write-side aggregate. Its state is rebuilt by replaying events; each
 * command method checks business rules and records new events.
 * It holds only what's needed to make decisions, not what screens display.
 */
public class BankAccount {

    private UUID id;
    private BigDecimal balance = BigDecimal.ZERO;
    private boolean closed;
    private long version;

    private final List<AccountEvent> pendingEvents = new ArrayList<>();
    private final Clock clock;

    private BankAccount(Clock clock) {
        this.clock = clock;
    }

    public static BankAccount open(UUID id, String owner, BigDecimal initialDeposit, Clock clock) {
        if (owner == null || owner.isBlank()) {
            throw new DomainException("Owner is required");
        }
        requireNonNegative(initialDeposit);
        BankAccount account = new BankAccount(clock);
        account.record(new AccountOpened(id, owner, initialDeposit, clock.instant()));
        return account;
    }

    public static BankAccount replay(List<AccountEvent> history, Clock clock) {
        BankAccount account = new BankAccount(clock);
        history.forEach(event -> {
            account.apply(event);
            account.version++;
        });
        return account;
    }

    public void deposit(BigDecimal amount) {
        requireOpen();
        requirePositive(amount);
        record(new MoneyDeposited(id, amount, clock.instant()));
    }

    public void withdraw(BigDecimal amount) {
        requireOpen();
        requirePositive(amount);
        if (balance.compareTo(amount) < 0) {
            throw new DomainException("Insufficient funds: balance " + balance + ", requested " + amount);
        }
        record(new MoneyWithdrawn(id, amount, clock.instant()));
    }

    public void close() {
        requireOpen();
        if (balance.signum() != 0) {
            throw new DomainException("Balance must be zero to close the account (balance " + balance + ")");
        }
        record(new AccountClosed(id, clock.instant()));
    }

    private void record(AccountEvent event) {
        apply(event);
        pendingEvents.add(event);
    }

    private void apply(AccountEvent event) {
        switch (event) {
            case AccountOpened e -> {
                id = e.accountId();
                balance = e.initialDeposit();
            }
            case MoneyDeposited e -> balance = balance.add(e.amount());
            case MoneyWithdrawn e -> balance = balance.subtract(e.amount());
            case AccountClosed e -> closed = true;
        }
    }

    private void requireOpen() {
        if (closed) {
            throw new DomainException("Account " + id + " is closed");
        }
    }

    private static void requirePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new DomainException("Amount must be positive");
        }
    }

    private static void requireNonNegative(BigDecimal amount) {
        if (amount == null || amount.signum() < 0) {
            throw new DomainException("Initial deposit cannot be negative");
        }
    }

    public UUID id() { return id; }
    public BigDecimal balance() { return balance; }
    public boolean isClosed() { return closed; }

    /** Version as loaded from the store (before pending events). */
    public long version() { return version; }

    public List<AccountEvent> pendingEvents() { return List.copyOf(pendingEvents); }
}

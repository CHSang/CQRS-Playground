package com.playground.bank.query.projection;

import com.playground.bank.event.AccountEvent;
import com.playground.bank.event.AccountEvent.AccountClosed;
import com.playground.bank.event.AccountEvent.AccountOpened;
import com.playground.bank.event.AccountEvent.MoneyDeposited;
import com.playground.bank.event.AccountEvent.MoneyWithdrawn;
import com.playground.bank.query.model.AccountSummary;
import com.playground.bank.query.model.AccountSummaryRepository;
import com.playground.bank.query.model.AccountTransaction;
import com.playground.bank.query.model.AccountTransactionRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Turns events into read models.
 *
 * It runs synchronously in the command's transaction, so reads are
 * consistent right after a write. In a distributed setup this would sit
 * behind a message broker and be eventually consistent.
 */
@Component
public class AccountProjection {

    private final AccountSummaryRepository summaries;
    private final AccountTransactionRepository transactions;

    public AccountProjection(AccountSummaryRepository summaries, AccountTransactionRepository transactions) {
        this.summaries = summaries;
        this.transactions = transactions;
    }

    @EventListener
    public void on(AccountEvent event) {
        switch (event) {
            case AccountOpened e -> {
                summaries.save(new AccountSummary(e.accountId(), e.owner(), e.initialDeposit(), e.occurredAt()));
                transactions.save(new AccountTransaction(e.accountId(), "OPENING_DEPOSIT",
                        e.initialDeposit(), e.initialDeposit(), e.occurredAt()));
            }
            case MoneyDeposited e -> movement(e.accountId(), "DEPOSIT", e.amount(), e.occurredAt());
            case MoneyWithdrawn e -> movement(e.accountId(), "WITHDRAWAL", e.amount().negate(), e.occurredAt());
            case AccountClosed e -> summary(e.accountId()).markClosed(e.occurredAt());
        }
    }

    private void movement(UUID accountId, String type, BigDecimal delta, Instant at) {
        AccountSummary summary = summary(accountId);
        summary.applyMovement(delta, at);
        transactions.save(new AccountTransaction(accountId, type, delta.abs(), summary.getBalance(), at));
    }

    private AccountSummary summary(UUID accountId) {
        return summaries.findById(accountId)
                .orElseThrow(() -> new IllegalStateException("No read model for account " + accountId));
    }
}

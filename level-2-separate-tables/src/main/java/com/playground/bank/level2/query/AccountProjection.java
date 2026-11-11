package com.playground.bank.level2.query;

import com.playground.bank.level2.event.AccountOpened;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Keeps the read table in sync with the write table.
 *
 * @EventListener runs synchronously inside the command's transaction, so
 * both tables commit (or roll back) together and a read right after a
 * write always sees the change.
 */
@Component
public class AccountProjection {

    private final AccountSummaryRepository summaries;

    public AccountProjection(AccountSummaryRepository summaries) {
        this.summaries = summaries;
    }

    @EventListener
    public void on(AccountOpened event) {
        summaries.save(new AccountSummary(event.accountId(), event.owner(), event.initialDeposit(),
                event.openedAt()));
    }
}

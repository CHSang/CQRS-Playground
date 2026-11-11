package com.playground.bank.level2.command;

import com.playground.bank.level2.event.AccountOpened;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Write side: saves to the write table, then publishes an event so the
 * read table can be updated. It doesn't know the read table exists.
 */
@Service
public class AccountCommandHandler {

    private final AccountRepository accounts;
    private final ApplicationEventPublisher events;

    public AccountCommandHandler(AccountRepository accounts, ApplicationEventPublisher events) {
        this.accounts = accounts;
        this.events = events;
    }

    @Transactional
    public UUID handle(OpenAccount command) {
        Account account = accounts.save(new Account(command.owner(), command.initialDeposit()));
        events.publishEvent(new AccountOpened(account.getId(), account.getOwner(), account.getBalance(),
                Instant.now()));
        return account.getId();
    }
}

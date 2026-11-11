package com.playground.bank.level3.command;

import com.playground.bank.level3.event.AccountOpened;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Write side: saves to the write database, then raises an event.
 * AccountEventPublisher sends it to RabbitMQ once this transaction commits.
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

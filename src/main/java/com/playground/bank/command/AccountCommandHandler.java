package com.playground.bank.command;

import com.playground.bank.command.AccountCommands.CloseAccount;
import com.playground.bank.command.AccountCommands.Deposit;
import com.playground.bank.command.AccountCommands.OpenAccount;
import com.playground.bank.command.AccountCommands.Withdraw;
import com.playground.bank.command.domain.BankAccount;
import com.playground.bank.common.NotFoundException;
import com.playground.bank.event.AccountEvent;
import com.playground.bank.eventstore.EventStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Write side: load aggregate from events -> run command -> append new events.
 * Never reads from the query-side tables.
 */
@Service
@Transactional
public class AccountCommandHandler {

    private final EventStore eventStore;
    private final Clock clock;

    public AccountCommandHandler(EventStore eventStore, Clock clock) {
        this.eventStore = eventStore;
        this.clock = clock;
    }

    public UUID handle(OpenAccount command) {
        UUID id = UUID.randomUUID();
        BankAccount account = BankAccount.open(id, command.owner(), command.initialDeposit(), clock);
        eventStore.append(id, 0, account.pendingEvents());
        return id;
    }

    public void handle(Deposit command) {
        update(command.accountId(), account -> account.deposit(command.amount()));
    }

    public void handle(Withdraw command) {
        update(command.accountId(), account -> account.withdraw(command.amount()));
    }

    public void handle(CloseAccount command) {
        update(command.accountId(), BankAccount::close);
    }

    private void update(UUID accountId, Consumer<BankAccount> action) {
        List<AccountEvent> history = eventStore.load(accountId);
        if (history.isEmpty()) {
            throw new NotFoundException("Account " + accountId + " not found");
        }
        BankAccount account = BankAccount.replay(history, clock);
        action.accept(account);
        eventStore.append(accountId, account.version(), account.pendingEvents());
    }
}

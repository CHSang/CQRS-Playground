package com.playground.bank.level1.command;

import com.playground.bank.level1.account.Account;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Write side: changes data and returns only the new id, never account data.
 * Reading is the query side's job.
 */
@Service
public class AccountCommandHandler {

    private final AccountRepository accounts;

    public AccountCommandHandler(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @Transactional
    public UUID handle(OpenAccount command) {
        Account account = new Account(command.owner(), command.initialDeposit());
        accounts.save(account);
        return account.getId();
    }
}

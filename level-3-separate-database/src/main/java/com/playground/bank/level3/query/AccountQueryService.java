package com.playground.bank.level3.query;

import com.playground.bank.level3.common.NotFoundException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.UUID;

/** Read side: reads only the read database, changes nothing. */
@Service
public class AccountQueryService {

    private final JdbcClient readDb;

    public AccountQueryService(JdbcClient readDb) {
        this.readDb = readDb;
    }

    public AccountSummary getAccount(UUID id) {
        return readDb.sql("select account_id, owner, balance, opened_at from account_summary where account_id = :id")
                .param("id", id)
                .query(AccountSummary.class)
                .optional()
                .orElseThrow(() -> new NotFoundException("Account " + id + " not found"));
    }
}

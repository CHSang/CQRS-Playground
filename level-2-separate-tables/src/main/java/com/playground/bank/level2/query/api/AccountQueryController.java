package com.playground.bank.level2.query.api;

import com.playground.bank.level2.query.AccountQueryService;
import com.playground.bank.level2.query.AccountSummary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/** Read API: GET only. */
@RestController
@RequestMapping("/api/accounts")
public class AccountQueryController {

    private final AccountQueryService queries;

    public AccountQueryController(AccountQueryService queries) {
        this.queries = queries;
    }

    @GetMapping("/{id}")
    public AccountSummary getAccount(@PathVariable UUID id) {
        return queries.getAccount(id);
    }
}

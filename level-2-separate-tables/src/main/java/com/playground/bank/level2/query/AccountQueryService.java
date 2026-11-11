package com.playground.bank.level2.query;

import com.playground.bank.level2.common.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** Read side: reads only the read table, changes nothing. */
@Service
public class AccountQueryService {

    private final AccountSummaryRepository summaries;

    public AccountQueryService(AccountSummaryRepository summaries) {
        this.summaries = summaries;
    }

    @Transactional(readOnly = true)
    public AccountSummary getAccount(UUID id) {
        return summaries.findById(id).orElseThrow(() -> new NotFoundException("Account " + id + " not found"));
    }
}

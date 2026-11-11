package com.playground.bank.level1.query;

import com.playground.bank.level1.common.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/** Read side: returns data, changes nothing. */
@Service
public class AccountQueryService {

    private final AccountViewRepository views;

    public AccountQueryService(AccountViewRepository views) {
        this.views = views;
    }

    @Transactional(readOnly = true)
    public AccountView getAccount(UUID id) {
        return views.findViewById(id).orElseThrow(() -> new NotFoundException("Account " + id + " not found"));
    }
}

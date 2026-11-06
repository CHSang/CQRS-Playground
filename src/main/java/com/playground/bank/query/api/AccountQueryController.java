package com.playground.bank.query.api;

import com.playground.bank.common.NotFoundException;
import com.playground.bank.eventstore.EventStore;
import com.playground.bank.eventstore.StoredEvent;
import com.playground.bank.query.model.AccountSummary;
import com.playground.bank.query.model.AccountSummaryRepository;
import com.playground.bank.query.model.AccountTransaction;
import com.playground.bank.query.model.AccountTransactionRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/** Read API: served only from read models, never from the aggregate. */
@RestController
@RequestMapping("/api/accounts")
@Transactional(readOnly = true)
public class AccountQueryController {

    private final AccountSummaryRepository summaries;
    private final AccountTransactionRepository transactions;
    private final EventStore eventStore;

    public AccountQueryController(AccountSummaryRepository summaries, AccountTransactionRepository transactions,
                                  EventStore eventStore) {
        this.summaries = summaries;
        this.transactions = transactions;
        this.eventStore = eventStore;
    }

    @GetMapping
    public List<AccountSummary> all() {
        return summaries.findAllByOrderByOpenedAtAsc();
    }

    @GetMapping("/{id}")
    public AccountSummary one(@PathVariable UUID id) {
        return summaries.findById(id).orElseThrow(() -> new NotFoundException("Account " + id + " not found"));
    }

    @GetMapping("/{id}/transactions")
    public List<AccountTransaction> statement(@PathVariable UUID id) {
        one(id);
        return transactions.findByAccountIdOrderByIdAsc(id);
    }

    /** For learning: the raw events the account's state is derived from. */
    @GetMapping("/{id}/events")
    public List<StoredEvent> events(@PathVariable UUID id) {
        List<StoredEvent> events = eventStore.loadRaw(id);
        if (events.isEmpty()) {
            throw new NotFoundException("Account " + id + " not found");
        }
        return events;
    }
}

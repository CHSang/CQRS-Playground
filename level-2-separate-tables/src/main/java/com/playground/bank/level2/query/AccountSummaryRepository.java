package com.playground.bank.level2.query;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/** Read-side repository over the "account_summary" table. */
public interface AccountSummaryRepository extends JpaRepository<AccountSummary, UUID> {
}

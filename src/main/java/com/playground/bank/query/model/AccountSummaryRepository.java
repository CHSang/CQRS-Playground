package com.playground.bank.query.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountSummaryRepository extends JpaRepository<AccountSummary, UUID> {

    List<AccountSummary> findAllByOrderByOpenedAtAsc();
}

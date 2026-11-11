package com.playground.bank.level2.command;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/** Write-side repository over the "account" table. */
public interface AccountRepository extends JpaRepository<Account, UUID> {
}

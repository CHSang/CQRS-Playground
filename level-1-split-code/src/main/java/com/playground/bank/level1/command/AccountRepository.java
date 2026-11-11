package com.playground.bank.level1.command;

import com.playground.bank.level1.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/** Write-side repository: loads and saves the entity. Only commands use it. */
public interface AccountRepository extends JpaRepository<Account, UUID> {
}

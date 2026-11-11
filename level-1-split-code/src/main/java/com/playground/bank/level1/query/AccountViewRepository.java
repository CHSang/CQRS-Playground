package com.playground.bank.level1.query;

import com.playground.bank.level1.account.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Read-side repository over the same "account" table. It extends the bare
 * Repository (no save/delete) and selects straight into AccountView, so the
 * query side can't change data and never sees the entity.
 */
public interface AccountViewRepository extends Repository<Account, UUID> {

    @Query("select new com.playground.bank.level1.query.AccountView(a.id, a.owner, a.balance, a.openedAt)"
            + " from Account a where a.id = :id")
    Optional<AccountView> findViewById(UUID id);
}

package com.playground.bank.level3.query;

import com.playground.bank.level3.event.AccountOpened;
import com.playground.bank.level3.messaging.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

/**
 * Reads events from RabbitMQ and updates the read database.
 * Runs separately from the command, so the read side lags slightly behind.
 */
@Component
public class AccountProjection {

    private final JdbcClient readDb;

    public AccountProjection(JdbcClient readDb) {
        this.readDb = readDb;
    }

    @RabbitListener(queues = RabbitConfig.ACCOUNT_EVENTS)
    public void on(AccountOpened event) {
        // MERGE (insert or update) so a message delivered twice doesn't fail
        readDb.sql("""
                merge into account_summary (account_id, owner, balance, opened_at)
                key (account_id)
                values (:id, :owner, :balance, :openedAt)
                """)
                .param("id", event.accountId())
                .param("owner", event.owner())
                .param("balance", event.initialDeposit())
                .param("openedAt", event.openedAt())
                .update();
    }
}

package com.playground.bank.level3.messaging;

import com.playground.bank.level3.event.AccountOpened;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Sends events to RabbitMQ only after the write transaction commits,
 * so the read side never hears about an account that was rolled back.
 *
 * Demo shortcut: if the app crashes between the commit and the send,
 * the event is lost. Real systems fix this with the outbox pattern.
 */
@Component
public class AccountEventPublisher {

    private final RabbitTemplate rabbit;

    public AccountEventPublisher(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @TransactionalEventListener // runs AFTER_COMMIT by default
    public void on(AccountOpened event) {
        rabbit.convertAndSend(RabbitConfig.ACCOUNT_EVENTS, event);
    }
}

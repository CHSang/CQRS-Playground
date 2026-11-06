package com.playground.bank.command.api;

import com.playground.bank.command.AccountCommandHandler;
import com.playground.bank.command.AccountCommands.CloseAccount;
import com.playground.bank.command.AccountCommands.Deposit;
import com.playground.bank.command.AccountCommands.OpenAccount;
import com.playground.bank.command.AccountCommands.Withdraw;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

/** Write API: only accepts commands, returns no account state. */
@RestController
@RequestMapping("/api/accounts")
public class AccountCommandController {

    private final AccountCommandHandler handler;

    public AccountCommandController(AccountCommandHandler handler) {
        this.handler = handler;
    }

    public record AmountRequest(@NotNull @DecimalMin(value = "0.00", inclusive = false) BigDecimal amount) {
    }

    @PostMapping
    public ResponseEntity<Map<String, UUID>> open(@Valid @RequestBody OpenAccount command) {
        UUID id = handler.handle(command);
        return ResponseEntity.created(URI.create("/api/accounts/" + id)).body(Map.of("accountId", id));
    }

    @PostMapping("/{id}/deposits")
    public ResponseEntity<Void> deposit(@PathVariable UUID id, @Valid @RequestBody AmountRequest request) {
        handler.handle(new Deposit(id, request.amount()));
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{id}/withdrawals")
    public ResponseEntity<Void> withdraw(@PathVariable UUID id, @Valid @RequestBody AmountRequest request) {
        handler.handle(new Withdraw(id, request.amount()));
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<Void> close(@PathVariable UUID id) {
        handler.handle(new CloseAccount(id));
        return ResponseEntity.accepted().build();
    }
}

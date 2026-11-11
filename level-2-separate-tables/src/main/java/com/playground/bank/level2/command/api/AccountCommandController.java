package com.playground.bank.level2.command.api;

import com.playground.bank.level2.command.AccountCommandHandler;
import com.playground.bank.level2.command.OpenAccount;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping
    public ResponseEntity<Map<String, UUID>> open(@Valid @RequestBody OpenAccount command) {
        UUID id = handler.handle(command);
        return ResponseEntity.created(URI.create("/api/accounts/" + id)).body(Map.of("accountId", id));
    }
}

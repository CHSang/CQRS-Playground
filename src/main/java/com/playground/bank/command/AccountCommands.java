package com.playground.bank.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

/** Intentions sent to the write side. They may be rejected. */
public final class AccountCommands {

    private AccountCommands() {
    }

    public record OpenAccount(@NotBlank String owner,
                              @NotNull @DecimalMin("0.00") BigDecimal initialDeposit) {
    }

    public record Deposit(UUID accountId, @NotNull @DecimalMin(value = "0.00", inclusive = false) BigDecimal amount) {
    }

    public record Withdraw(UUID accountId, @NotNull @DecimalMin(value = "0.00", inclusive = false) BigDecimal amount) {
    }

    public record CloseAccount(UUID accountId) {
    }
}

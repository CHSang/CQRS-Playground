package com.playground.bank.level3.command;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/** The command: an intention sent to the write side. */
public record OpenAccount(@NotBlank String owner,
                          @NotNull @DecimalMin("0.00") BigDecimal initialDeposit) {
}

package ru.gazprombank.payhub.middleservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateTransferRequestDto(@NotNull String from,
                                       @NotNull String to,
                                       @NotNull @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$") String amount) {
}

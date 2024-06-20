package ru.gazprombank.payhub.middleservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAccountRequestDto(@NotBlank @Size(min = 3, max = 255) String accountName) {
}

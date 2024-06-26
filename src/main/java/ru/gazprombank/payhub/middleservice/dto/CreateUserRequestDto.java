package ru.gazprombank.payhub.middleservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequestDto(@NotNull Long userId, @Size(min = 3, max = 255) String userName) {
}

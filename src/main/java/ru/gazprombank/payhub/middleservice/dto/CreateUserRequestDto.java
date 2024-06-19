package ru.gazprombank.payhub.middleservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequestDto(@NotNull Long userId, @NotBlank @Size(min = 3, max = 255) String userName) {
}

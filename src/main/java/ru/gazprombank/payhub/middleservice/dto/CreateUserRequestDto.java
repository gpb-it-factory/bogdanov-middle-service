package ru.gazprombank.payhub.middleservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequestDto(
        @NotNull(message = "не должно равняться null")
        Long userId,
        @NotBlank(message = "не должно быть пустым")
        @Size(min = 3, max = 255, message = "размер должен находиться в диапазоне от 3 до 255")
        String userName) {
}

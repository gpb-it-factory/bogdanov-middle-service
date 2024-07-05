package ru.gazprombank.payhub.middleservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateTransferRequestDto(
        @NotBlank(message = "не должно быть пустым")
        @Size(min = 3, max = 255, message = "размер должен находиться в диапазоне от 3 до 255")
        String from,
        @NotBlank(message = "не должно быть пустым")
        @Size(min = 3, max = 255, message = "размер должен находиться в диапазоне от 3 до 255")
        String to,
        @NotNull(message = "не должно равняться null")
        @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$", message = "Неверный формат суммы")
        String amount) {
}

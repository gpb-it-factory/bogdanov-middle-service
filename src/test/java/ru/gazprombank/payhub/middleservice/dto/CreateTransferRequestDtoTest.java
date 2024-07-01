package ru.gazprombank.payhub.middleservice.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.gazprombank.payhub.middleservice.util.TestDataUtils.*;

public class CreateTransferRequestDtoTest {
    private final Validator validator;

    public CreateTransferRequestDtoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    @DisplayName("Успешное создание CreateTransferRequestDto")
    void testSuccessfulCreateTransferRequestDto() {
        final String from = "Alice";
        final String to = "Bob";
        final String amount = "100.00";

        CreateTransferRequestDto dto = new CreateTransferRequestDto(from, to, amount);

        assertValid(dto);
    }

    @Test
    @DisplayName("Создание CreateTransferRequestDto с пустым from")
    void testCreateTransferRequestDtoWithBlankFrom() {
        final String from = " ";
        final String to = "Bob";
        final String amount = "100.00";

        CreateTransferRequestDto dto = new CreateTransferRequestDto(from, to, amount);

        assertInvalid(dto, MUST_NOT_BE_BLANK);
    }

    @Test
    @DisplayName("Создание CreateTransferRequestDto с коротким from")
    void testCreateTransferRequestDtoWithShortFrom() {
        final String from = "Al";
        final String to = "Bob";
        final String amount = "100.00";

        CreateTransferRequestDto dto = new CreateTransferRequestDto(from, to, amount);

        assertInvalid(dto, SIZE_MUST_BE_BETWEEN_3_AND_255);
    }

    @Test
    @DisplayName("Создание CreateTransferRequestDto с длинным from")
    void testCreateTransferRequestDtoWithLongFrom() {
        final String from = "A".repeat(256);
        final String to = "Bob";
        final String amount = "100.00";

        CreateTransferRequestDto dto = new CreateTransferRequestDto(from, to, amount);

        assertInvalid(dto, SIZE_MUST_BE_BETWEEN_3_AND_255);
    }

    @Test
    @DisplayName("Создание CreateTransferRequestDto с пустым to")
    void testCreateTransferRequestDtoWithBlankTo() {
        final String from = "Alice";
        final String to = " ";
        final String amount = "100.00";

        CreateTransferRequestDto dto = new CreateTransferRequestDto(from, to, amount);

        assertInvalid(dto, MUST_NOT_BE_BLANK);
    }

    @Test
    @DisplayName("Создание CreateTransferRequestDto с коротким to")
    void testCreateTransferRequestDtoWithShortTo() {
        final String from = "Alice";
        final String to = "Bo";
        final String amount = "100.00";

        CreateTransferRequestDto dto = new CreateTransferRequestDto(from, to, amount);

        assertInvalid(dto, SIZE_MUST_BE_BETWEEN_3_AND_255);
    }

    @Test
    @DisplayName("Создание CreateTransferRequestDto с длинным to")
    void testCreateTransferRequestDtoWithLongTo() {
        final String from = "Alice";
        final String to = "B".repeat(256);
        final String amount = "100.00";

        CreateTransferRequestDto dto = new CreateTransferRequestDto(from, to, amount);

        assertInvalid(dto, SIZE_MUST_BE_BETWEEN_3_AND_255);
    }

    @Test
    @DisplayName("Создание CreateTransferRequestDto с null amount")
    void testCreateTransferRequestDtoWithNullAmount() {
        final String from = "Alice";
        final String to = "Bob";
        final String amount = null;

        CreateTransferRequestDto dto = new CreateTransferRequestDto(from, to, amount);

        assertInvalid(dto, MUST_NOT_BE_NULL);
    }

    @Test
    @DisplayName("Создание CreateTransferRequestDto с неверным форматом amount")
    void testCreateTransferRequestDtoWithInvalidAmountFormat() {
        final String from = "Alice";
        final String to = "Bob";
        final String amount = "100.000";

        CreateTransferRequestDto dto = new CreateTransferRequestDto(from, to, amount);

        assertInvalid(dto, "Неверный формат суммы");
    }

    private void assertValid(final CreateTransferRequestDto dto) {
        Set<ConstraintViolation<CreateTransferRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no constraint violations");
    }

    private void assertInvalid(final CreateTransferRequestDto dto, final String expectedMessage) {
        Set<ConstraintViolation<CreateTransferRequestDto>> violations = validator.validate(dto);
        Set<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        assertTrue(messages.contains(expectedMessage), "Ожидаемое сообщение " + expectedMessage);
    }
}
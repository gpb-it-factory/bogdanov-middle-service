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

public class CreateUserRequestDtoTest {

    public static final String MUST_NOT_BE_BLANK = "не должно быть пустым";
    public static final String SIZE_MUST_BE_BETWEEN_3_AND_255 = "размер должен находиться в диапазоне от 3 до 255";
    public static final String MUST_NOT_BE_NULL = "не должно равняться null";

    private final Validator validator;

    public CreateUserRequestDtoTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    @DisplayName("Успешное создание CreateUserRequestDto")
    void testSuccessfulCreateUserRequestDto() {
        final Long userId = 12345L;
        final String userName = "testUserName";

        CreateUserRequestDto dto = new CreateUserRequestDto(userId, userName);

        assertValid(dto);
    }

    @Test
    @DisplayName("Создание CreateUserRequestDto с null userId")
    void testCreateUserRequestDtoWithNullUserId() {
        final Long userId = null;
        final String userName = "testUserName";

        CreateUserRequestDto dto = new CreateUserRequestDto(userId, userName);

        assertInvalid(dto, MUST_NOT_BE_NULL);
    }

    @Test
    @DisplayName("Создание CreateUserRequestDto с пустым userName")
    void testCreateUserRequestDtoWithBlankUserName() {
        final Long userId = 12345L;
        final String userName = "";

        CreateUserRequestDto dto = new CreateUserRequestDto(userId, userName);

        assertInvalid(dto, MUST_NOT_BE_BLANK);
    }

    @Test
    @DisplayName("Создание CreateUserRequestDto с коротким userName")
    void testCreateUserRequestDtoWithShortUserName() {
        final Long userId = 12345L;
        final String userName = "ab";

        CreateUserRequestDto dto = new CreateUserRequestDto(userId, userName);

        assertInvalid(dto, SIZE_MUST_BE_BETWEEN_3_AND_255);
    }

    @Test
    @DisplayName("Создание CreateUserRequestDto с длинным userName")
    void testCreateUserRequestDtoWithLongUserName() {
        final Long userId = 12345L;
        final String userName = "a".repeat(256);

        CreateUserRequestDto dto = new CreateUserRequestDto(userId, userName);

        assertInvalid(dto, SIZE_MUST_BE_BETWEEN_3_AND_255);
    }

    private void assertValid(final CreateUserRequestDto dto) {
        Set<ConstraintViolation<CreateUserRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Expected no constraint violations");
    }

    private void assertInvalid(final CreateUserRequestDto dto, final String expectedMessage) {
        Set<ConstraintViolation<CreateUserRequestDto>> violations = validator.validate(dto);
        Set<String> messages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());
        assertTrue(messages.contains(expectedMessage), "Expected constraint violation message: " + expectedMessage);
    }
}
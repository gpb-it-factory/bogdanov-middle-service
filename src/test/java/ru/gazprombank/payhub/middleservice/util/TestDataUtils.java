package ru.gazprombank.payhub.middleservice.util;

import ru.gazprombank.payhub.middleservice.dto.CreateTransferRequestDto;
import ru.gazprombank.payhub.middleservice.dto.CreateUserRequestDto;

public final class TestDataUtils {

    public static final String MUST_NOT_BE_BLANK = "не должно быть пустым";
    public static final String SIZE_MUST_BE_BETWEEN_3_AND_255 = "размер должен находиться в диапазоне от 3 до 255";
    public static final String MUST_NOT_BE_NULL = "не должно равняться null";
    private TestDataUtils() {
    }

    public static CreateUserRequestDto createCreateUserRequestDto(final Long userId, final String testName) {
        return new CreateUserRequestDto(userId, testName);
    }

    public static CreateTransferRequestDto createCreateTransferRequestDto(String fromAccount, String toAccount, String amount) {
        return new CreateTransferRequestDto(fromAccount, toAccount, amount);
    }
}

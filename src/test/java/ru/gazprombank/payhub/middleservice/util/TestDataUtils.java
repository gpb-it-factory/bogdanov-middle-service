package ru.gazprombank.payhub.middleservice.util;

import ru.gazprombank.payhub.middleservice.dto.CreateUserRequestDto;

public final class TestDataUtils {
    private TestDataUtils() {
    }

    public static CreateUserRequestDto createCreateUserRequestDto(final Long userId, final String testName) {
        return new CreateUserRequestDto(userId, testName);
    }
}

package ru.gazprombank.payhub.middleservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gazprombank.payhub.middleservice.client.UserClient;
import ru.gazprombank.payhub.middleservice.dto.CreateUserRequestDto;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public String create(@Valid @RequestBody CreateUserRequestDto createUserRequestDto) {
        log.info("Create user: {}", createUserRequestDto);
        userClient.create(createUserRequestDto);
        return String.format("Клиент %s зарегистрирован в банке", createUserRequestDto.userName());
    }
}

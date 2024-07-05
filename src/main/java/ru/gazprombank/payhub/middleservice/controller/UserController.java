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
import ru.gazprombank.payhub.middleservice.dto.ResponseMessage;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseMessage create(@Valid @RequestBody CreateUserRequestDto createUserRequestDto) {
        log.info("Create user: {}", createUserRequestDto.userId());
        userClient.create(createUserRequestDto);
        return new ResponseMessage(String.format("Клиент %s зарегистрирован в банке", createUserRequestDto.userId()));
    }
}

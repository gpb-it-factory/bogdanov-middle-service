package ru.gazprombank.payhub.middleservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.gazprombank.payhub.middleservice.client.AccountClient;
import ru.gazprombank.payhub.middleservice.dto.CreateAccountRequestDto;

@Slf4j
@RestController
@RequestMapping("/api/v1/users/{id}/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountClient accountClient;

    @PostMapping
    public String createAccount(@PathVariable("id") Long userId, @RequestBody CreateAccountRequestDto requestDto) {
        log.info("Create user: {}", requestDto.accountName());
        accountClient.create(userId, requestDto);
        return String.format("Аккаунт %s создан", requestDto.accountName());
    }
}

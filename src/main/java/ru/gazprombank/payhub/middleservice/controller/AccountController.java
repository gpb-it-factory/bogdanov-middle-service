package ru.gazprombank.payhub.middleservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.gazprombank.payhub.middleservice.client.AccountClient;
import ru.gazprombank.payhub.middleservice.dto.CreateAccountRequestDto;
import ru.gazprombank.payhub.middleservice.dto.ResponseMessage;

import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/users/{id}/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountClient accountClient;

    @PostMapping
    public ResponseMessage create(@PathVariable("id") Long userId,
                                  @RequestBody @Valid CreateAccountRequestDto requestDto) {
        if (requestDto.accountName().isEmpty() || requestDto.accountName().isBlank()) {
            requestDto = new CreateAccountRequestDto("Акционный");
        }
        log.info("Create account: {}", requestDto.accountName());
        accountClient.create(userId, requestDto);
        return new ResponseMessage(String.format("Аккаунт %s создан", requestDto.accountName()));
    }

    @GetMapping
    public ResponseMessage get(@PathVariable("id") String userId) {
        log.info("Получение счетов для user c userId: {}", userId);
        String response = accountClient.get(userId).stream()
                .map(account -> String.format("Остаток на счете %s: %s", account.accountName(), account.amount()))
                .collect(Collectors.joining(System.lineSeparator()));
        return new ResponseMessage(response);
    }
}

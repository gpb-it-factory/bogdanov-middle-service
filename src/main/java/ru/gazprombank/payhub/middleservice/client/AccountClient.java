package ru.gazprombank.payhub.middleservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.gazprombank.payhub.middleservice.dto.AccountsResponseDto;
import ru.gazprombank.payhub.middleservice.dto.CreateAccountRequestDto;

import java.util.List;

@FeignClient(name = "accountClient", url = "${backend-server.url}")
public interface AccountClient {
    @PostMapping("/v2/users/{id}/accounts")
    void create(@PathVariable("id") Long userId,
                @RequestBody CreateAccountRequestDto createAccountRequestDto);

    @GetMapping("/v2/users/{id}/accounts")
    List<AccountsResponseDto> get(@PathVariable("id") String userId);
}

package ru.gazprombank.payhub.middleservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.gazprombank.payhub.middleservice.dto.CreateUserRequestDto;

@FeignClient(name = "userClient", url = "${backend-server.url}")
public interface UserClient {
    @PostMapping("/v2/users")
    void create(@RequestBody CreateUserRequestDto userRequestDto);
}

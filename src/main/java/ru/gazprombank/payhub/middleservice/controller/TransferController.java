package ru.gazprombank.payhub.middleservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.gazprombank.payhub.middleservice.client.TransferClient;
import ru.gazprombank.payhub.middleservice.dto.CreateTransferRequestDto;
import ru.gazprombank.payhub.middleservice.dto.ResponseMessage;

@Slf4j
@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
public class TransferController {
    private final TransferClient transferClient;

    @PostMapping
    public ResponseMessage createAccount(@RequestBody @Valid CreateTransferRequestDto transferDto) {
        log.info("Create transfer: {}", transferDto.from());
        if(transferDto.from().equals(transferDto.to())) {
            return new ResponseMessage("Вы не можете совершить перевод на собственный счет");
        }
        transferClient.create(transferDto);
        return new ResponseMessage(String.format("перевод на сумму %s совершен", transferDto.amount()));
    }
}

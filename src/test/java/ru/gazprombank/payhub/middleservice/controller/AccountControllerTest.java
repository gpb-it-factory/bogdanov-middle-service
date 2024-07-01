package ru.gazprombank.payhub.middleservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.gazprombank.payhub.middleservice.client.AccountClient;
import ru.gazprombank.payhub.middleservice.dto.CreateAccountRequestDto;
import ru.gazprombank.payhub.middleservice.dto.ResponseMessage;
import ru.gazprombank.payhub.middleservice.dto.AccountsResponseDto;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AccountClient accountClient;
    @Autowired
    private ObjectMapper objectMapper;
    @BeforeEach
    void setUp() {
        Mockito.reset(accountClient);
    }
    @Test
    @DisplayName("Успешное создание аккаунта")
    void testCreateAccount() throws Exception {
        final Long userId = 12345L;
        final String accountName = "Сберегательный";
        final CreateAccountRequestDto requestDto = new CreateAccountRequestDto(accountName);
        final String expectedMessage = String.format("Аккаунт %s создан", accountName);

        Mockito.doNothing().when(accountClient).create(eq(userId), any(CreateAccountRequestDto.class));

        mockMvc.perform(post("/api/v1/users/{id}/accounts", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new ResponseMessage(expectedMessage))));
    }

    @Test
    @DisplayName("Создание аккаунта с пустым именем аккаунта")
    void testCreateAccountWithBlankAccountName() throws Exception {
        final Long userId = 12345L;
        final String accountName = "";
        final CreateAccountRequestDto requestDto = new CreateAccountRequestDto(accountName);
        final String expectedMessage = String.format("Аккаунт %s создан", "Акционный");

        Mockito.doNothing().when(accountClient).create(eq(userId), any(CreateAccountRequestDto.class));

        mockMvc.perform(post("/api/v1/users/{id}/accounts", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new ResponseMessage(expectedMessage))));
    }

    @Test
    @DisplayName("Получение аккаунтов пользователя")
    void testGetAccounts() throws Exception {
        final String userId = "12345";
        final String accountId = "1";
        final String accountName = "Сберегательный";
        final String amount = "1000.00";
        final String expectedMessage = String.format("Остаток на счете %s: %s", accountName, amount);

        Mockito.when(accountClient.get(userId)).thenReturn(List.of(new AccountsResponseDto(accountId, accountName, amount)));

        mockMvc.perform(get("/api/v1/users/{id}/accounts", userId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new ResponseMessage(expectedMessage))));
    }
}
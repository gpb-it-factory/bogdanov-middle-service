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
import ru.gazprombank.payhub.middleservice.client.TransferClient;
import ru.gazprombank.payhub.middleservice.dto.CreateTransferRequestDto;
import ru.gazprombank.payhub.middleservice.dto.ResponseMessage;
import ru.gazprombank.payhub.middleservice.dto.TransferResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.gazprombank.payhub.middleservice.util.TestDataUtils.*;

@WebMvcTest(TransferController.class)
public class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferClient transferClient;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Mockito.reset(transferClient);
    }

    @Test
    @DisplayName("Успешное создание перевода")
    void testCreateTransferSuccess() throws Exception {
        final String fromAccount = "account-123";
        final String toAccount = "account-456";
        final String amount = "100.00";
        final CreateTransferRequestDto requestDto = createCreateTransferRequestDto(fromAccount, toAccount, amount);
        final String expectedMessage = String.format("перевод на сумму %s совершен", amount);

        Mockito.when(transferClient.create(any(CreateTransferRequestDto.class)))
                .thenReturn(new TransferResponse("transfer-123"));

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new ResponseMessage(expectedMessage))));
    }

    @Test
    @DisplayName("Ошибка при создании перевода на собственный счет")
    void testCreateTransferToOwnAccount() throws Exception {
        final String sameAccount = "account-123";
        final String amount = "100.00";
        final CreateTransferRequestDto requestDto = createCreateTransferRequestDto(sameAccount, sameAccount, amount);
        final String expectedMessage = "Вы не можете совершить перевод на собственный счет";

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new ResponseMessage(expectedMessage))));
    }

    @Test
    @DisplayName("Ошибка при создании перевода с пустым полем 'from'")
    void testCreateTransferWithBlankFrom() throws Exception {
        final String fromAccount = "";
        final String toAccount = "account-456";
        final String amount = "100.00";
        final CreateTransferRequestDto requestDto = createCreateTransferRequestDto(fromAccount, toAccount, amount);

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ResponseMessage("from: " + MUST_NOT_BE_BLANK))));
    }

    @Test
    @DisplayName("Ошибка при создании перевода с коротким 'from'")
    void testCreateTransferWithShortFrom() throws Exception {
        final String fromAccount = "ac";
        final String toAccount = "account-456";
        final String amount = "100.00";
        final CreateTransferRequestDto requestDto = createCreateTransferRequestDto(fromAccount, toAccount, amount);

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ResponseMessage("from: " + SIZE_MUST_BE_BETWEEN_3_AND_255))));
    }

    @Test
    @DisplayName("Ошибка при создании перевода с длинным 'from'")
    void testCreateTransferWithLongFrom() throws Exception {
        final String fromAccount = "a".repeat(256);
        final String toAccount = "account-456";
        final String amount = "100.00";
        final CreateTransferRequestDto requestDto = createCreateTransferRequestDto(fromAccount, toAccount, amount);

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ResponseMessage("from: " + SIZE_MUST_BE_BETWEEN_3_AND_255))));
    }


    @Test
    @DisplayName("Ошибка при создании перевода с пустым полем 'to'")
    void testCreateTransferWithBlankTo() throws Exception {
        final String fromAccount = "account-123";
        final String toAccount = "";
        final String amount = "100.00";
        final CreateTransferRequestDto requestDto = createCreateTransferRequestDto(fromAccount, toAccount, amount);

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ResponseMessage("to: " + MUST_NOT_BE_BLANK))));
    }

    @Test
    @DisplayName("Ошибка при создании перевода с коротким 'to'")
    void testCreateTransferWithShortTo() throws Exception {
        final String fromAccount = "account-123";
        final String toAccount = "ac";
        final String amount = "100.00";
        final CreateTransferRequestDto requestDto = createCreateTransferRequestDto(fromAccount, toAccount, amount);

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ResponseMessage("to: " + SIZE_MUST_BE_BETWEEN_3_AND_255))));
    }

    @Test
    @DisplayName("Ошибка при создании перевода с длинным 'to'")
    void testCreateTransferWithLongTo() throws Exception {
        final String fromAccount = "account-123";
        final String toAccount = "a".repeat(256);
        final String amount = "100.00";
        final CreateTransferRequestDto requestDto = createCreateTransferRequestDto(fromAccount, toAccount, amount);

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ResponseMessage("to: " + SIZE_MUST_BE_BETWEEN_3_AND_255))));
    }

    @Test
    @DisplayName("Ошибка при создании перевода с неверным форматом суммы")
    void testCreateTransferWithInvalidAmount() throws Exception {
        final String fromAccount = "account-123";
        final String toAccount = "account-456";
        final String amount = "invalid-amount";
        final CreateTransferRequestDto requestDto = createCreateTransferRequestDto(fromAccount, toAccount, amount);

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString
                        (new ResponseMessage("amount: " + "Неверный формат суммы"))));
    }

    @Test
    @DisplayName("Ошибка при создании перевода с отсутствующим 'amount'")
    void testCreateTransferWithNullAmount() throws Exception {
        final String fromAccount = "account-123";
        final String toAccount = "account-456";
        final CreateTransferRequestDto requestDto = createCreateTransferRequestDto(fromAccount, toAccount, null);

        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(
                        new ResponseMessage("amount: " + MUST_NOT_BE_NULL))));
    }
}
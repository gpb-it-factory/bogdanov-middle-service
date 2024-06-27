package ru.gazprombank.payhub.middleservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.gazprombank.payhub.middleservice.dto.CreateUserRequestDto;
import ru.gazprombank.payhub.middleservice.dto.ResponseMessage;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static ru.gazprombank.payhub.middleservice.util.TestDataUtils.createCreateUserRequestDto;

@SpringBootTest
@AutoConfigureWireMock
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private UserController userController;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Создание user")
    void testCreateUser() throws Exception {
        WireMock.stubFor(
                WireMock.post(WireMock.urlEqualTo("/v2/users"))
                        .willReturn(WireMock.aResponse().withStatus(204)));
        final Long userId = 12345L;
        final String testName = "testName";
        final CreateUserRequestDto userDto = createCreateUserRequestDto(userId, testName);
        final String expectedMessage = String.format("Клиент %s зарегистрирован в банке", userDto.userName());

        String responseContent = mockMvc.perform(post("/api/v1/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        ResponseMessage responseMessage = mapper.readValue(responseContent, ResponseMessage.class);

        assertEquals(responseMessage.message(), expectedMessage);
    }

    @Test
    @DisplayName("Создание уже существующего user")
    void testCreateDuplicateUser() throws Exception {
        WireMock.stubFor(
                WireMock.post(WireMock.urlEqualTo("/v2/users"))
                        .willReturn(WireMock.aResponse().withStatus(409)));

        final Long userId = 12345L;
        final String testName = "testName";
        final CreateUserRequestDto userDto = createCreateUserRequestDto(userId, testName);
        final String expectedMessage = "Пользователь уже зарегистрирован";

        String responseContent = mockMvc.perform(post("/api/v1/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        ResponseMessage responseMessage = mapper.readValue(responseContent, ResponseMessage.class);

        assertEquals(responseMessage.message(), expectedMessage);
    }
}
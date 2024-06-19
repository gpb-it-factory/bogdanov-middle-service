package ru.gazprombank.payhub.middleservice.controller;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWireMock
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {
    @Autowired
    private UserController userController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Создание user")
    void testCreateUser() {
        WireMock.stubFor(
                WireMock.post(WireMock.urlEqualTo("/v2/users"))
                        .willReturn(WireMock.aResponse().withStatus(204)));
        final Long userId = 12345L;
        final String testName = "testName";
        CreateUserRequestDto userDto = new CreateUserRequestDto(userId, testName);
        final String expectedMessage = String.format("Клиент %s зарегистрирован в банке", userDto.userName());

        String response = userController.create(userDto);

        WireMock.verify(
                WireMock.postRequestedFor(WireMock.urlEqualTo("/v2/users"))
                        .withRequestBody(WireMock.equalToJson(String.format(
                                """
                                        {"userId":%d,
                                        "userName":"%s"}
                                        """,
                                userId,
                                testName)))
        );
        assertEquals(expectedMessage, response);
    }

    @Test
    @DisplayName("Создание уже существующего user")
    void testCreateDuplicateUser() throws Exception {
        WireMock.stubFor(
                WireMock.post(WireMock.urlEqualTo("/v2/users"))
                        .willReturn(WireMock.aResponse().withStatus(409)));

        String json = """
                {"userId":123,
                "userName":"testUser"}
                """;
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("Пользователь уже зарегистрирован"));

        WireMock.verify(
                WireMock.postRequestedFor(WireMock.urlEqualTo("/v2/users"))
                        .withRequestBody(WireMock.equalToJson(json))
        );
    }
}
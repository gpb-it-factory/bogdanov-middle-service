package ru.gazprombank.payhub.middleservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.gazprombank.payhub.middleservice.controller.UserController;
import ru.gazprombank.payhub.middleservice.dto.CreateUserRequestDto;
import ru.gazprombank.payhub.middleservice.dto.ResponseMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.gazprombank.payhub.middleservice.util.TestDataUtils.createCreateUserRequestDto;

@AutoConfigureWireMock
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerIntegrationTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private UserController userController;
    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Создание user")
    void testCreateUser() throws Exception {
        WireMock.stubFor(
                WireMock.post(WireMock.urlEqualTo("/v2/users"))
                        .willReturn(WireMock.aResponse().withStatus(204)));
        final Long userId = 12345L;
        final String testName = "testName";
        final CreateUserRequestDto userDto = createCreateUserRequestDto(userId, testName);
        final String expectedMessage = String.format("Клиент %s зарегистрирован в банке", userDto.userId());

        final ResponseMessage response = webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(userDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseMessage.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertEquals(response.message(), expectedMessage);
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
        final String expectedMessage = "Вы уже зарегистрировались";

        final ResponseMessage response = webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(userDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseMessage.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertEquals(response.message(), expectedMessage);
    }
}
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
import ru.gazprombank.payhub.middleservice.dto.CreateAccountRequestDto;
import ru.gazprombank.payhub.middleservice.dto.ResponseMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureWireMock
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountControllerIntegrationTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Создание аккаунта")
    void testCreateAccount() throws Exception {
        WireMock.stubFor(
                WireMock.post(WireMock.urlEqualTo("/v2/users/12345/accounts"))
                        .willReturn(WireMock.aResponse().withStatus(204)));

        final Long userId = 12345L;
        final String accountName = "Сберегательный";
        final CreateAccountRequestDto requestDto = new CreateAccountRequestDto(accountName);
        final String expectedMessage = String.format("Аккаунт %s создан", accountName);

        final ResponseMessage response = webTestClient.post()
                .uri("/api/v1/users/{id}/accounts", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(requestDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseMessage.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertEquals(response.message(), expectedMessage);
    }

    @Test
    @DisplayName("Создание аккаунта с пустым именем аккаунта")
    void testCreateAccountWithBlankAccountName() throws Exception {
        WireMock.stubFor(
                WireMock.post(WireMock.urlEqualTo("/v2/users/12345/accounts"))
                        .willReturn(WireMock.aResponse().withStatus(204)));

        final Long userId = 12345L;
        final String accountName = "";
        final CreateAccountRequestDto requestDto = new CreateAccountRequestDto(accountName);
        final String expectedMessage = String.format("Аккаунт %s создан", "Акционный");

        final ResponseMessage response = webTestClient.post()
                .uri("/api/v1/users/{id}/accounts", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(requestDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseMessage.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertEquals(response.message(), expectedMessage);
    }

    @Test
    @DisplayName("Получение счетов для пользователя")
    void testGetAccounts() throws Exception {
        WireMock.stubFor(
                WireMock.get(WireMock.urlEqualTo("/v2/users/12345/accounts"))
                        .willReturn(WireMock.aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("[{\"accountId\": \"1\", \"accountName\": \"Сберегательный\", " +
                                          "\"amount\": \"1000\"}, {\"accountId\": \"2\", " +
                                          "\"accountName\": \"Кредитный\", \"amount\": \"5000\"}]")));

        final String userId = "12345";
        final String expectedResponse = String.format(
                "Остаток на счете Сберегательный: 1000%sОстаток на счете Кредитный: 5000", System.lineSeparator());

        final ResponseMessage response = webTestClient.get()
                .uri("/api/v1/users/{id}/accounts", userId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseMessage.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertEquals(expectedResponse, response.message());
    }
}

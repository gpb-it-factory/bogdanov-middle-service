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
import ru.gazprombank.payhub.middleservice.dto.CreateTransferRequestDto;
import ru.gazprombank.payhub.middleservice.dto.ResponseMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureWireMock
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransferControllerIntegrationTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Создание перевода")
    void testCreateTransfer() throws Exception {
        WireMock.stubFor(
                WireMock.post(WireMock.urlEqualTo("/v2/transfers"))
                        .willReturn(WireMock.aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"transferId\": \"transfer-123\"}")));

        final String fromAccount = "account-123";
        final String toAccount = "account-456";
        final String amount = "100.00";
        final CreateTransferRequestDto requestDto = new CreateTransferRequestDto(fromAccount, toAccount, amount);
        final String expectedMessage = String.format("перевод на сумму %s совершен", amount);

        final ResponseMessage response = webTestClient.post()
                .uri("/api/v1/transfers")
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
    @DisplayName("Ошибка при создании перевода на собственный счет")
    void testCreateTransferToOwnAccount() throws Exception {
        final String sameAccount = "account-123";
        final String amount = "100.00";
        final CreateTransferRequestDto requestDto = new CreateTransferRequestDto(sameAccount, sameAccount, amount);
        final String expectedMessage = "Вы не можете совершить перевод на собственный счет";

        final ResponseMessage response = webTestClient.post()
                .uri("/api/v1/transfers")
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
    @DisplayName("Ошибка при создании перевода с коротким 'from'")
    void testCreateTransferWithShortFrom() throws Exception {
        final String fromAccount = "ac";
        final String toAccount = "account-456";
        final String amount = "100.00";
        final CreateTransferRequestDto requestDto = new CreateTransferRequestDto(fromAccount, toAccount, amount);

        final ResponseMessage response = webTestClient.post()
                .uri("/api/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(requestDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseMessage.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertEquals(response.message(), "from: размер должен находиться в диапазоне от 3 до 255");
    }

    @Test
    @DisplayName("Ошибка при создании перевода с длинным 'from'")
    void testCreateTransferWithLongFrom() throws Exception {
        final String fromAccount = "a".repeat(256); // 256 символов
        final String toAccount = "account-456";
        final String amount = "100.00";
        final CreateTransferRequestDto requestDto = new CreateTransferRequestDto(fromAccount, toAccount, amount);

        final ResponseMessage response = webTestClient.post()
                .uri("/api/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(requestDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseMessage.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertEquals(response.message(), "from: размер должен находиться в диапазоне от 3 до 255");
    }

    @Test
    @DisplayName("Ошибка при создании перевода с коротким 'to'")
    void testCreateTransferWithShortTo() throws Exception {
        final String fromAccount = "account-123";
        final String toAccount = "ac";
        final String amount = "100.00";
        final CreateTransferRequestDto requestDto = new CreateTransferRequestDto(fromAccount, toAccount, amount);

        final ResponseMessage response = webTestClient.post()
                .uri("/api/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(requestDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseMessage.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertEquals(response.message(), "to: размер должен находиться в диапазоне от 3 до 255");
    }

    @Test
    @DisplayName("Ошибка при создании перевода с длинным 'to'")
    void testCreateTransferWithLongTo() throws Exception {
        final String fromAccount = "account-123";
        final String toAccount = "a".repeat(256); // 256 символов
        final String amount = "100.00";
        final CreateTransferRequestDto requestDto = new CreateTransferRequestDto(fromAccount, toAccount, amount);

        final ResponseMessage response = webTestClient.post()
                .uri("/api/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(requestDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseMessage.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertEquals(response.message(), "to: размер должен находиться в диапазоне от 3 до 255");
    }

    @Test
    @DisplayName("Ошибка при создании перевода с отсутствующим 'amount'")
    void testCreateTransferWithNullAmount() throws Exception {
        final String fromAccount = "account-123";
        final String toAccount = "account-456";
        final CreateTransferRequestDto requestDto = new CreateTransferRequestDto(fromAccount, toAccount, null);

        final ResponseMessage response = webTestClient.post()
                .uri("/api/v1/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(mapper.writeValueAsString(requestDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseMessage.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(response);
        assertEquals(response.message(), "amount: не должно равняться null");
    }
}

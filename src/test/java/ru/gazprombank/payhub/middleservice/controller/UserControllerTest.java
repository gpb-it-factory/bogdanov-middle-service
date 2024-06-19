package ru.gazprombank.payhub.middleservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.gazprombank.payhub.middleservice.client.UserClient;
import ru.gazprombank.payhub.middleservice.dto.CreateUserRequestDto;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();
    @MockBean
    private UserClient userClient;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Парсинг JSON в CreateUserRequestDto")
    void createUser() throws Exception {
        final Long userId = 12345L;
        final String testName = "testName";
        CreateUserRequestDto userDto = new CreateUserRequestDto(userId, testName);
        String expectedStringISO = new String(
                String.format("Клиент %s зарегистрирован в банке", userDto.userName()).getBytes(),
                StandardCharsets.ISO_8859_1
        );

        mockMvc.perform(post("/api/v1/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedStringISO));
    }

    @Test
    @DisplayName("Тест невалидного JSON, userName isBlank")
    void testInvalidJson() throws Exception {
        String invalidJson = "{\"userId\": 12345, \"userName\":\"\"}";

        mockMvc.perform(post("/api/v1/users")
                        .content(invalidJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResolvedException())
                        .isInstanceOf(MethodArgumentNotValidException.class));
    }
}
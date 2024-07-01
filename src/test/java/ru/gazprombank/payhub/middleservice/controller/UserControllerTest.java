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
import ru.gazprombank.payhub.middleservice.client.UserClient;
import ru.gazprombank.payhub.middleservice.dto.CreateUserRequestDto;
import ru.gazprombank.payhub.middleservice.dto.ResponseMessage;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Mockito.reset(userClient);
    }

    public static final String MUST_NOT_BE_BLANK = "не должно быть пустым";
    public static final String SIZE_MUST_BE_BETWEEN_3_AND_255 = "размер должен находиться в диапазоне от 3 до 255";
    public static final String MUST_NOT_BE_NULL = "не должно равняться null";

    @Test
    @DisplayName("Успешное создание пользователя")
    void testCreateUser() throws Exception {
        final Long userId = 12345L;
        final String userName = "testUserName";
        final CreateUserRequestDto requestDto = new CreateUserRequestDto(userId, userName);
        final String expectedMessage = String.format("Клиент %s зарегистрирован в банке", userId);

        Mockito.doNothing().when(userClient).create(any(CreateUserRequestDto.class));

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new ResponseMessage(expectedMessage))));
    }

    @Test
    @DisplayName("Создание пользователя с пустым userName")
    void testCreateUserWithBlankUserName() throws Exception {
        final Long userId = 12345L;
        final String userName = "";
        final CreateUserRequestDto requestDto = new CreateUserRequestDto(userId, userName);
        final String expectedMessage = "userName: " + MUST_NOT_BE_BLANK;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new ResponseMessage(expectedMessage))));
    }

    @Test
    @DisplayName("Создание пользователя с коротким userName")
    void testCreateUserWithShortUserName() throws Exception {
        final Long userId = 12345L;
        final String userName = "ab";
        final CreateUserRequestDto requestDto = new CreateUserRequestDto(userId, userName);
        final String expectedMessage = "userName: " + SIZE_MUST_BE_BETWEEN_3_AND_255;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new ResponseMessage(expectedMessage))));
    }

    @Test
    @DisplayName("Создание пользователя с длинным userName")
    void testCreateUserWithLongUserName() throws Exception {
        final Long userId = 12345L;
        final String userName = "a".repeat(256);
        final CreateUserRequestDto requestDto = new CreateUserRequestDto(userId, userName);
        final String expectedMessage = "userName: " + SIZE_MUST_BE_BETWEEN_3_AND_255;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new ResponseMessage(expectedMessage))));
    }

    @Test
    @DisplayName("Создание пользователя с null userId")
    void testCreateUserWithNullUserId() throws Exception {
        final Long userId = null;
        final String userName = "testUserName";
        final CreateUserRequestDto requestDto = new CreateUserRequestDto(userId, userName);
        final String expectedMessage = "userId: " + MUST_NOT_BE_NULL;

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new ResponseMessage(expectedMessage))));
    }
}
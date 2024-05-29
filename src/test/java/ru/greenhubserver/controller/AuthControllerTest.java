package ru.greenhubserver.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import ru.greenhubserver.dto.controller.IdDto;
import ru.greenhubserver.dto.security.JwtRequestDto;
import ru.greenhubserver.dto.security.JwtResponseDto;
import ru.greenhubserver.dto.security.RegistrationUserDto;
import ru.greenhubserver.exceptions.BadRequestException;
import ru.greenhubserver.service.AuthService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void authenticationSuccess() throws Exception {
        JwtRequestDto authRequest = new JwtRequestDto("user", "password");
        JwtResponseDto authResponse = new JwtResponseDto("jwt-token");

        Mockito.when(authService.createAuthToken(Mockito.any(JwtRequestDto.class)))
                .thenReturn(authResponse);

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(authResponse)));
    }

    @Test
    void authenticationFailure() throws Exception {
        JwtRequestDto authRequest = new JwtRequestDto("user", "wrongpassword");

        Mockito.when(authService.createAuthToken(Mockito.any(JwtRequestDto.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Invalid credentials")));
    }

    @Test
    void registrationSuccess() throws Exception {
        RegistrationUserDto registrationUserDto = new RegistrationUserDto("newUser", "newPassword", "newUser@example.com");
        Long userId = 123L;

        Mockito.when(authService.createNewUser(Mockito.any(RegistrationUserDto.class)))
                .thenReturn(new IdDto(userId));

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationUserDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void registrationFailure() throws Exception {
        RegistrationUserDto registrationUserDto = new RegistrationUserDto("newUser", "newPassword", "newUser@example.com");

        Mockito.when(authService.createNewUser(Mockito.any(RegistrationUserDto.class)))
                .thenThrow(new BadRequestException("User already exists"));

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationUserDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validateJwtRequestDto() throws Exception {
        JwtRequestDto invalidDto = new JwtRequestDto("asdasdasd", "");
        String json = objectMapper.writeValueAsString(invalidDto);

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Password mustn't be blank\n"));;
    }

    @Test
    void validateRegistrationUserDto() throws Exception {
        RegistrationUserDto invalidDto = new RegistrationUserDto("", "123123", "email@email.ru");
        String json = objectMapper.writeValueAsString(invalidDto);

        mockMvc.perform(post("/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }
}


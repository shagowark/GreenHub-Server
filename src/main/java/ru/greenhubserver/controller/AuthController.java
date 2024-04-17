package ru.greenhubserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.greenhubserver.dto.controller.IdDto;
import ru.greenhubserver.dto.security.JwtRequestDto;
import ru.greenhubserver.dto.security.JwtResponseDto;
import ru.greenhubserver.dto.security.RegistrationUserDto;
import ru.greenhubserver.service.AuthService;

@RestController
@RequiredArgsConstructor
@Tag(name = "API аутентификации", description = "Позволяет регистрироваться и авторизовываться, используя технологии JWT токенов")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/auth")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Регистрирует нового пользователя")
    public JwtResponseDto authentication(@RequestBody JwtRequestDto authRequest) {
        return authService.createAuthToken(authRequest);
    }

    @PostMapping(value = "/registration")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Авторизует пользователя (возвращает JWT токен)")
    public IdDto registration(@RequestBody RegistrationUserDto registrationUserDto) {
        return authService.createNewUser(registrationUserDto);
    }
}

package ru.greenhubserver.dto.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationUserDto {
    private String username;
    private String password;
    private String email;
}

package ru.greenhubserver.dto.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtRequestDto {
    @NotBlank(message = "Name mustn't be blank")
    @Size(max = 10, message = "Name must be <=10 characters")
    @Pattern(regexp = "^[a-zA-Z_](?=[\\w.]{2,19}$)\\w*\\.?\\w*$", message = "Wrong characters in username")
    private String username;

    @NotBlank(message = "Password mustn't be blank")
    @Size(max = 20, message = "Password must be <=20 characters")
    private String password;
}

package ru.greenhubserver.dto.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserChangesDto {
    @NotBlank(message = "Email mustn't be blank")
    @Size(max = 50, message = "Email must be <=50 characters")
    @Pattern(regexp = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\\b", message = "Wrong characters in email")
    private String email;
    private MultipartFile image;
}

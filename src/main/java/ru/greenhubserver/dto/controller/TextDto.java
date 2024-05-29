package ru.greenhubserver.dto.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TextDto {
    @NotBlank(message = "text mustn't be blank")
    @Size(max = 256, message = "text must be <=256 characters")
    private String text;
}

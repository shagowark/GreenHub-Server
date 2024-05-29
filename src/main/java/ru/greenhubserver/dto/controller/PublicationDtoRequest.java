package ru.greenhubserver.dto.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
@Builder
public class PublicationDtoRequest {
    @NotBlank(message = "title mustn't be blank")
    @Size(max = 20, message = "title must be <=20 characters")
    private String title;

    @NotBlank(message = "text mustn't be blank")
    @Size(max = 256, message = "text must be <=256 characters")
    private String text;

    private MultipartFile image;

    @Size(max = 7, message = "title must be <7 characters")
    private Set<String> tags;
}

package ru.greenhubserver.dto.controller;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
@Builder
public class PublicationDtoRequest {
    private String title;
    private String text;
    private MultipartFile image;
    private Set<String> tags;
}

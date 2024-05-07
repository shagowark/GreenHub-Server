package ru.greenhubserver.dto.controller;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserChangesDto {
    private String email;
    private MultipartFile image;
}

package ru.greenhubserver.dto.controller;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AchievementDto {
    private Long id;
    private byte[] image;
    private String name;
}

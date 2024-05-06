package ru.greenhubserver.dto.controller;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSmallDto {
    private Long userId;
    private byte[] userImage;
    private String username;
}

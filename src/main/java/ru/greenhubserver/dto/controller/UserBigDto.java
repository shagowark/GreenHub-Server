package ru.greenhubserver.dto.controller;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserBigDto {
    private Long id;
    private String username;
    private String email;
    private byte[] image;
    private Long subscriptionsCount;
    private Long subscribersCount;
}

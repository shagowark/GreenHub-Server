package ru.greenhubserver.dto.controller;

import lombok.Builder;
import lombok.Data;
import ru.greenhubserver.entity.Role;
import ru.greenhubserver.entity.State;

import java.util.Set;

@Data
@Builder
public class UserBigDto {
    private Long id;
    private String username;
    private String email;
    private byte[] image;
    private Long subscriptionsCount;
    private Long subscribersCount;
    private State state;
    private boolean isSubscribed;
    private Set<RoleDto> roles;
}

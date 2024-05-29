package ru.greenhubserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.greenhubserver.entity.Role;
import ru.greenhubserver.repository.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getUserRole(){
        return roleRepository.findByName("ROLE_USER").get();
    }
    public Role getAdminRole(){
        return roleRepository.findByName("ROLE_ADMIN").get();
    }

    public Role getModeratorRole() {
        return roleRepository.findByName("ROLE_MODERATOR").get();
    }
}

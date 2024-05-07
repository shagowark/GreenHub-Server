package ru.greenhubserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.greenhubserver.dto.controller.AchievementDto;
import ru.greenhubserver.dto.controller.UserBigDto;
import ru.greenhubserver.dto.controller.UserChangesDto;
import ru.greenhubserver.dto.controller.UserSmallDto;
import ru.greenhubserver.entity.Achievement;
import ru.greenhubserver.service.UserService;

import java.security.Principal;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Secured({"ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR"})
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserBigDto getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @GetMapping("/{id}/subscriptions")
    @ResponseStatus(HttpStatus.OK)
    public Set<UserSmallDto> getSubscriptions(@PathVariable Long id) {
        return userService.getUserSubscriptions(id);
    }

    @GetMapping("/{id}/subscribers")
    @ResponseStatus(HttpStatus.OK)
    public Set<UserSmallDto> getSubscribers(@PathVariable Long id) {
        return userService.getUserSubscribers(id);
    }

    @PostMapping("/{id}/ban")
    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_ADMIN", "ROLE_MODERATOR"})
    public void banUser(@PathVariable Long id) {
        userService.banUser(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void editUser(@PathVariable Long id, UserChangesDto dto, Principal principal) {
        userService.editUser(id, dto, principal);
    }

    @GetMapping("/{id}/achievements")
    @ResponseStatus(HttpStatus.OK)
    public Set<AchievementDto> getUserAchievements(@PathVariable Long id) {
        return userService.getUserAchievements(id);
    }

    @PatchMapping("/{id}/achievements")
    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_ADMIN", "ROLE_MODERATOR"})
    public void editUserAchievements(@PathVariable Long id, @RequestBody Set<String> achievements) {
        userService.editUserAchievements(id, achievements);
    }


}

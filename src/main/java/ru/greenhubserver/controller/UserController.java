package ru.greenhubserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "API пользователей", description = "Предоставляет эндпоинты для взаимодействия с пользователями," +
        " подписками/подписчиками, достижениями и повышением/понижением ролей")
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Возвращает пользователя")
    public UserBigDto getUser(@PathVariable Long id,
                              Principal principal) {
        userService.checkIfUserBanned(principal);
        return userService.getUser(id);
    }

    @GetMapping("/{id}/subscriptions")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Возвращает все подписки пользователя")
    public Set<UserSmallDto> getSubscriptions(@PathVariable Long id,
                                              Principal principal) {
        userService.checkIfUserBanned(principal);
        return userService.getUserSubscriptions(id);
    }

    @GetMapping("/{id}/subscribers")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Возвращает всех подписчиков пользователя")
    public Set<UserSmallDto> getSubscribers(@PathVariable Long id,
                                            Principal principal) {
        userService.checkIfUserBanned(principal);
        return userService.getUserSubscribers(id);
    }

    @PostMapping("/{id}/ban")
    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_ADMIN", "ROLE_MODERATOR"})
    @Operation(summary = "Банит пользователя, доступно только модератору и администратору")
    public void banUser(@PathVariable Long id,
                        Principal principal) {
        userService.checkIfUserBanned(principal);
        userService.banUser(id, principal);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Меняет данные пользователя")
    public void editUser(@PathVariable Long id, UserChangesDto dto,
                         Principal principal) {
        userService.checkIfUserBanned(principal);
        userService.editUser(id, dto, principal);
    }

    @GetMapping("/{id}/achievements")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Меняет данные пользователя")
    public Set<AchievementDto> getUserAchievements(@PathVariable Long id,
                                                   Principal principal) {
        userService.checkIfUserBanned(principal);
        return userService.getUserAchievements(id);
    }

    @PatchMapping("/{id}/achievements")
    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_ADMIN", "ROLE_MODERATOR"})
    @Operation(summary = "Меняет достижения пользователя, доступно только модератору и администратору")
    public void editUserAchievements(@PathVariable Long id,
                                     @RequestBody Set<String> achievements,
                                     Principal principal) {
        userService.checkIfUserBanned(principal);
        userService.editUserAchievements(id, achievements);
    }

    @PostMapping("/{id}/subscribe")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Подписывается на пользователя")
    public void subscribeToUser(@PathVariable Long id,
                                Principal principal) {
        userService.checkIfUserBanned(principal);
        userService.subscribeToUser(id, principal);
    }

    @PostMapping("/{id}/unsubscribe")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Отписывается от пользователя")
    public void unsubscribeToUser(@PathVariable Long id,
                                  Principal principal) {
        userService.checkIfUserBanned(principal);
        userService.unsubscribeToUser(id, principal);
    }

    @PostMapping("/{id}/upgrade")
    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_ADMIN"})
    @Operation(summary = "Повышает пользователя до модератора, доступно только администратору")
    public void upgradeUser(@PathVariable Long id,
                            Principal principal) {
        userService.checkIfUserBanned(principal);
        userService.upgradeUser(id, principal);
    }

    @PostMapping("/{id}/downgrade")
    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_ADMIN"})
    @Operation(summary = "Понижает модератора до обычного пользователя, доступно только администратору")
    public void downgradeUser(@PathVariable Long id,
                              Principal principal) {
        userService.checkIfUserBanned(principal);
        userService.downgradeUser(id, principal);
    }
}

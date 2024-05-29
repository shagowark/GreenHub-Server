package ru.greenhubserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.greenhubserver.dto.controller.*;
import ru.greenhubserver.service.CommentService;
import ru.greenhubserver.service.PublicationService;
import ru.greenhubserver.service.ReactionService;
import ru.greenhubserver.service.UserService;

import java.security.Principal;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/publications")
@Tag(name = "API публикаций", description = "Предоставляет эндпоинты для взаимодействия с публикациями." +
        " Все эндпоинты кроме GET /publications - защищены, пускают только авторизованных пользователей")
public class PublicationController {

    private final PublicationService publicationService;
    private final ReactionService reactionService;
    private final CommentService commentService;
    private final UserService userService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Возвращает все публикации, доступно в том числе для неавторизованных запросов")
    public Page<PublicationDtoResponse> getPublications(Pageable pageable,
                                                        @RequestParam(required = false) Set<String> tags,
                                                        Principal principal
    ) {
        if (principal != null) {
            userService.checkIfUserBanned(principal);
        }
        return publicationService.findPublications(pageable, tags, principal);
    }

    @GetMapping("/subscriptions")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Возвращает все публикации подписок пользователя")
    public Page<PublicationDtoResponse> getPublicationsFromSubscriptions(Pageable pageable,
                                                        @RequestParam(required = false) Set<String> tags,
                                                        Principal principal
    ) {
        if (principal != null) {
            userService.checkIfUserBanned(principal);
        }
        return publicationService.findPublicationsFromSubscriptions(pageable, tags, principal);
    }

    @GetMapping("/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Возвращает список всех публикаций определенного пользователя")
    public Page<PublicationDtoResponse> getPublicationsByUser(@PathVariable Long id,
                                                              Pageable pageable,
                                                              Principal principal) {
        userService.checkIfUserBanned(principal);
        return publicationService.findPublications(pageable, id, principal);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавляет публикацию")
    public void postPublication(@ModelAttribute @Valid PublicationDtoRequest publicationDtoRequest,
                                Principal principal) {
        userService.checkIfUserBanned(principal);
        publicationService.savePublication(publicationDtoRequest, principal);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаляет публикацию")
    public void deletePublicationById(@PathVariable Long id,
                                      Principal principal) {
        userService.checkIfUserBanned(principal);
        publicationService.deletePublication(id, principal);
    }

    @PostMapping("/{id}/ban")
    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_ADMIN", "ROLE_MODERATOR"})
    @Operation(summary = "Банит публикацию, доступно только модератору и администратору")
    public void banPublication(@PathVariable Long id,
                               Principal principal) {
        userService.checkIfUserBanned(principal);
        publicationService.banPublication(id);
    }

    @PostMapping("/{id}/reactions")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавляет реакцию на публикацию")
    public void postReaction(@PathVariable Long id,
                             @RequestBody @Valid ReactionTypeDto reactionType,
                             Principal principal) {
        userService.checkIfUserBanned(principal);
        reactionService.saveReaction(id, reactionType.getReactionType(), principal);
    }

    @DeleteMapping("/{id}/reactions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаляет реакцию на публикацию")
    public void deleteReaction(@PathVariable Long id,
                               Principal principal) {
        userService.checkIfUserBanned(principal);
        reactionService.deleteReaction(id, principal);
    }

    @GetMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Возвращает все комментарии к публикации")
    public Page<CommentDto> getComments(@PathVariable Long id,
                                        Pageable pageable,
                                        Principal principal) {
        userService.checkIfUserBanned(principal);
        return commentService.findComments(id, pageable);
    }

    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавляет комментарий на публикацию")
    public void postComment(@PathVariable Long id,
                            @RequestBody @Valid TextDto textDto,
                            Principal principal) {
        userService.checkIfUserBanned(principal);
        commentService.saveComment(id, textDto.getText(), principal);
    }
}

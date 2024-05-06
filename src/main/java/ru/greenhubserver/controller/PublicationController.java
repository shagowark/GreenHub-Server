package ru.greenhubserver.controller;

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

import java.security.Principal;
import java.util.Set;

//TODO swagger
// htpps

@RestController
@RequiredArgsConstructor
@RequestMapping("/publications")
@Secured({"ROLE_USER", "ROLE_ADMIN", "ROLE_MODERATOR"})
public class PublicationController {

    private final PublicationService publicationService;
    private final ReactionService reactionService;
    private final CommentService commentService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Page<PublicationDtoResponse> getPublications(Pageable pageable, @RequestParam(required = false) Set<String> tags) {
        return publicationService.findPublications(pageable, tags);
    }

    @GetMapping("/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Page<PublicationDtoResponse> getPublicationsByUser(@PathVariable Long id, Pageable pageable) {
        return publicationService.findPublications(pageable, id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void postPublication(@ModelAttribute PublicationDtoRequest publicationDtoRequest, Principal principal) {
        publicationService.savePublication(publicationDtoRequest, principal);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePublicationById(@PathVariable Long id, Principal principal) {
        publicationService.deletePublication(id, principal);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Secured({"ROLE_ADMIN", "ROLE_MODERATOR"})
    public void banPublication(@PathVariable Long id) {
        publicationService.banPublication(id);
    }

    @PostMapping("/{id}/reactions")
    @ResponseStatus(HttpStatus.CREATED)
    public void postReaction(@PathVariable Long id, @RequestBody ReactionTypeDto reactionType, Principal principal){
        reactionService.saveReaction(id, reactionType.getReactionType(), principal);
    }

    @DeleteMapping("/{id}/reactions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReaction(@PathVariable Long id, Principal principal){
        reactionService.deleteReaction(id, principal);
    }

    @GetMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.OK)
    public Page<CommentDto> postComment(@PathVariable Long id, Pageable pageable) {
        return commentService.findComments(id, pageable);
    }

    @PostMapping("/{id}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public void postComment(@PathVariable Long id, @RequestBody TextDto textDto, Principal principal) {
        commentService.saveComment(id, textDto.getText(), principal);
    }
}

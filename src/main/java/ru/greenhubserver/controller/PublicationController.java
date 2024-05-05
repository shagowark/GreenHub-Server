package ru.greenhubserver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import ru.greenhubserver.dto.controller.IdDto;
import ru.greenhubserver.dto.controller.PublicationDtoRequest;
import ru.greenhubserver.dto.controller.PublicationDtoResponse;
import ru.greenhubserver.service.PublicationService;

import java.security.Principal;
import java.util.Set;

//TODO swagger

@RestController
@RequiredArgsConstructor
@RequestMapping("/publications")
@Secured({"ROLE_USER", "ROLE_ADMIN"})
public class PublicationController {

    private final PublicationService publicationService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public Page<PublicationDtoResponse> getPublications(Pageable pageable,@RequestParam(required = false) Set<String> tags) {
        return publicationService.findPublications(pageable, tags);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public IdDto postPublication(@ModelAttribute PublicationDtoRequest publicationDtoRequest, Principal principal){
        publicationService.savePublication(publicationDtoRequest, principal);
        return null;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePublicationById(@PathVariable Long id){
         publicationService.deletePublication(id);
    }
}

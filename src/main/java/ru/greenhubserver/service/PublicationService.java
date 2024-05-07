package ru.greenhubserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.greenhubserver.dto.controller.PublicationDtoRequest;
import ru.greenhubserver.dto.controller.PublicationDtoResponse;
import ru.greenhubserver.dto.controller.UserSmallDto;
import ru.greenhubserver.entity.*;
import ru.greenhubserver.exceptions.NoRightsException;
import ru.greenhubserver.exceptions.NotFoundException;
import ru.greenhubserver.repository.PublicationRepository;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicationService {
    private final PublicationRepository publicationRepository;
    private final ImageCloudService imageCloudService;
    private final UserService userService;
    private final TagService tagService;
    private final ImageService imageService;
    private final RoleService roleService;


    public void savePublication(PublicationDtoRequest publicationDtoRequest, Principal principal) {
        Image image = new Image();
        image = imageService.save(image);
        image.setName(imageCloudService.generateFileName(image.getId(), publicationDtoRequest.getImage()));
        imageService.save(image);
        imageCloudService.saveImage(publicationDtoRequest.getImage(), image.getName());

        Publication publication = Publication.builder()
                .image(image)
                .state(State.VISIBLE)
                .title(publicationDtoRequest.getTitle())
                .text(publicationDtoRequest.getText())
                .tags(publicationDtoRequest.getTags().stream().map(tagService::getTagByName).collect(Collectors.toSet()))
                .rating(0L)
                .commentsCount(0L)
                .reactions(new HashSet<>())
                .user(userService.findByUserName(principal.getName()))
                .state(State.VISIBLE)
                .build();

        publicationRepository.save(publication);
    }

    public void savePublication(Publication publication) {
        publicationRepository.save(publication);
    }

    public Page<PublicationDtoResponse> findPublications(Pageable pageable, Set<String> tagNames) {
        Page<Publication> found;
        found = tagNames == null ? publicationRepository.findAll(pageable) : publicationRepository.findAllByTags(pageable, tagNames);
        return new PageImpl<>(publicationToDto(found));
    }

    public Page<PublicationDtoResponse> findPublications(Pageable pageable, Long userId) {
        Page<Publication> found = publicationRepository.findAllByUser(userService.findById(userId), pageable);
        return new PageImpl<>(publicationToDto(found));
    }

    public void deletePublication(Long id, Principal principal) {
        User user = userService.findByUserName(principal.getName());
        if (!findPublicationById(id).getUser().equals(user)
                || user.getRoles().contains(roleService.getModeratorRole())
                || user.getRoles().contains(roleService.getAdminRole())) {
            throw new NoRightsException("Cannot delete other's publication if you're not amin or moder");
        }
        publicationRepository.deleteById(id);
    }

    public void banPublication(Long id) {
        Publication publication = findPublicationById(id);
        publication.setState(State.BANNED);
        publicationRepository.save(publication);
    }

    public Publication findPublicationById(Long id) {
        return publicationRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Publication not found")
        );
    }

    private List<PublicationDtoResponse> publicationToDto(Page<Publication> found) {
        List<PublicationDtoResponse> res = new ArrayList<>();
        for (Publication entity : found) {
            if (entity.getState() == State.BANNED) continue;

            PublicationDtoResponse dto = PublicationDtoResponse.builder()
                    .title(entity.getTitle())
                    .text(entity.getText())
                    .tags(entity.getTags().stream().map(Tag::getName).collect(Collectors.toSet()))
                    .image(imageCloudService.getImage(entity.getImage().getName()))
                    .rating(entity.getRating())
                    .commentsCount(entity.getCommentsCount())
                    .author(UserSmallDto.builder()
                            .userId(entity.getUser().getId())
                            .username(entity.getUser().getUsername())
                            .userImage(imageCloudService.getImage(entity.getUser().getImage().getName())).build())
                    .build();

            res.add(dto);
        }
        return res;
    }
}

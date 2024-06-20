package ru.greenhubserver.service;

import jakarta.transaction.Transactional;
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


    @Transactional
    public void savePublication(PublicationDtoRequest publicationDtoRequest, Principal principal) {
        Image image = null;
        if (publicationDtoRequest.getImage() != null) {
            image = new Image();
            image = imageService.save(image);
            image.setName(imageCloudService.generateFileName(image.getId(), publicationDtoRequest.getImage()));
            imageService.save(image);
            imageCloudService.saveImage(publicationDtoRequest.getImage(), image.getName());
        }

        Publication publication = Publication.builder()
                .image(image)
                .state(State.VISIBLE)
                .title(publicationDtoRequest.getTitle())
                .text(publicationDtoRequest.getText())
                .tags(publicationDtoRequest.getTags().stream().map(tagService::getTagByName).collect(Collectors.toSet()))
                .rating(0L)
                .commentsCount(0L)
                .reactions(new HashSet<>())
                .user(userService.findByUsername(principal.getName()))
                .state(State.VISIBLE)
                .build();

        publicationRepository.save(publication);
    }

    @Transactional
    public void savePublication(Publication publication) {
        publicationRepository.save(publication);
    }

    public Page<PublicationDtoResponse> findPublications(Pageable pageable, Set<String> tagNames, Principal principal) {
        Page<Publication> found;
        User user = null;
        if (principal != null) {
            user = userService.findByUsername(principal.getName());
        }
        found = tagNames == null
                ? publicationRepository.findAll(pageable)
                : publicationRepository.findAllByTags(pageable, tagNames.stream().map(tagService::getTagByName).collect(Collectors.toSet()));
        return new PageImpl<>(publicationToDto(found, user), pageable, found.getTotalElements());
    }

    public Page<PublicationDtoResponse> findPublications(Pageable pageable, Long userId, Principal principal) {
        Page<Publication> found = publicationRepository.findAllByUser(userService.findById(userId), pageable);
        User user = null;
        if (principal != null) {
            user = userService.findByUsername(principal.getName());
        }
        return new PageImpl<>(publicationToDto(found, user), pageable, found.getTotalElements());
    }

    public Page<PublicationDtoResponse> findPublicationsFromSubscriptions(Pageable pageable, Set<String> tagNames, Principal principal) {
        Page<Publication> found;
        User user = userService.findByUsername(principal.getName());
        Set<User> subscriptions = user.getSubscriptions();
        if (tagNames == null) {
            found = publicationRepository.findAllInUsers(pageable, subscriptions);
        } else {
            found = publicationRepository.findAllInUsersByTags(pageable, subscriptions, tagNames.stream().map(tagService::getTagByName).collect(Collectors.toSet()));
        }
        return new PageImpl<>(publicationToDto(found, user), pageable, found.getTotalElements());
    }

    @Transactional
    public void deletePublication(Long id, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (!findPublicationById(id).getUser().equals(user)
                && !(user.getRoles().contains(roleService.getModeratorRole())
                || user.getRoles().contains(roleService.getAdminRole()))) {
            throw new NoRightsException("Cannot delete other's publication if you're not amin or moder");
        }
        Publication publication = findPublicationById(id);
        if (publication.getImage() != null) {
            imageCloudService.deleteImage(publication.getImage().getName());
        }
        publicationRepository.deleteById(id);
    }

    @Transactional
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

    private List<PublicationDtoResponse> publicationToDto(Page<Publication> found, User user) {
        List<PublicationDtoResponse> res = new ArrayList<>();
        for (Publication entity : found) {
            if (entity.getState() == State.BANNED) continue;

            PublicationDtoResponse dto = PublicationDtoResponse.builder()
                    .id(entity.getId())
                    .title(entity.getTitle())
                    .text(entity.getText())
                    .tags(entity.getTags().stream().map(Tag::getName).collect(Collectors.toSet()))
                    .image(entity.getImage() != null ? imageCloudService.getImage(entity.getImage().getName()) : null)
                    .rating(entity.getRating())
                    .commentsCount(entity.getCommentsCount())
                    .createdTime(entity.getCreatedTime())
                    .author(UserSmallDto.builder()
                            .userId(entity.getUser().getId())
                            .username(entity.getUser().getUsername())
                            .userImage(imageCloudService.getImage(entity.getUser().getImage().getName())).build())
                    .build();
            if (user != null) {
                Reaction reaction = null;
                for (Reaction elem : entity.getReactions()){
                    if (elem.getUser() == user){
                        reaction = elem;
                        break;
                    }
                }
                dto.setReactionType(reaction == null ? null : reaction.getReactionType());
            }
            res.add(dto);
        }
        return res;
    }
}

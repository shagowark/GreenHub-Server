package ru.greenhubserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.greenhubserver.dto.controller.PublicationDtoRequest;
import ru.greenhubserver.dto.controller.PublicationDtoResponse;
import ru.greenhubserver.entity.Image;
import ru.greenhubserver.entity.Publication;
import ru.greenhubserver.entity.State;
import ru.greenhubserver.entity.Tag;
import ru.greenhubserver.repository.PublicationRepository;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicationService {
    private final PublicationRepository publicationRepository;
    private final ImageCloudService imageCloudService;
    private final UserService userService;
    private final TagService tagService;
    private final ImageService imageService;


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
                .user(userService.findByUserName(principal.getName()).orElse(null))
                .build();

        publicationRepository.save(publication);
    }

    public Page<PublicationDtoResponse> findPublications(Pageable pageable, Set<String> tagNames) {
        Page<Publication> found;
        found = tagNames == null ?  publicationRepository.findAll(pageable) : publicationRepository.findAllByTags(pageable, tagNames);



        List<PublicationDtoResponse> res = new ArrayList<>();
        for (Publication entity : found) {
            PublicationDtoResponse dto = PublicationDtoResponse.builder()
                    .title(entity.getTitle())
                    .text(entity.getText())
                    .tags(entity.getTags().stream().map(Tag::getName).collect(Collectors.toSet()))
                    .image(imageCloudService.getImage(entity.getImage().getName()))
                    .rating(entity.getRating())
                    .commentsCount(entity.getCommentsCount())
                    .authorId(entity.getUser().getId())
                    .authorName(entity.getUser().getUsername())
                    .authorImage(imageCloudService.getImage(entity.getUser().getImage().getName()))
                    .build();

            res.add(dto);
        }

        return new PageImpl<>(res);
    }

    public void deletePublication(Long id) {
        publicationRepository.deleteById(id);
    }
}

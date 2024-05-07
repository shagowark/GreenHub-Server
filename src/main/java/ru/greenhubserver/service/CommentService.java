package ru.greenhubserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.greenhubserver.dto.controller.CommentDto;
import ru.greenhubserver.entity.Comment;
import ru.greenhubserver.entity.Publication;
import ru.greenhubserver.entity.User;
import ru.greenhubserver.exceptions.NotFoundException;
import ru.greenhubserver.repository.CommentRepository;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PublicationService publicationService;
    private final ImageCloudService imageCloudService;


    public void saveComment(Long publicationId, String text, Principal principal) {
        User user = userService.findByUserName(principal.getName());
        Publication publication = publicationService.findPublicationById(publicationId);
        Comment comment = new Comment();
        comment.setText(text);
        comment.setPublication(publication);
        comment.setUser(user);

        publication.setCommentsCount(publication.getCommentsCount() + 1);
        publicationService.savePublication(publication);

        commentRepository.save(comment);
    }

    public Page<CommentDto> findComments(Long publicationId, Pageable pageable) {
        Page<Comment> found = commentRepository.findAllByPublication(publicationService.findPublicationById(publicationId), pageable);
        List<CommentDto> res = new ArrayList<>();
        for (Comment comment : found) {
            CommentDto dto = CommentDto.builder()
                    .id(comment.getId())
                    .text(comment.getText())
                    .authorName(comment.getUser().getUsername())
                    .authorImage(imageCloudService.getImage(comment.getUser().getImage().getName()))
                    .build();

            res.add(dto);
        }

        return new PageImpl<>(res);
    }
}

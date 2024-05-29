package ru.greenhubserver.dto.controller;

import lombok.Builder;
import lombok.Data;
import ru.greenhubserver.entity.ReactionType;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
public class PublicationDtoResponse {
    private Long id;
    private String title;
    private String text;
    private byte[] image;
    private Set<String> tags;
    private Long rating;
    private Long commentsCount;
    private UserSmallDto author;
    private ReactionType reactionType;
    private Instant createdTime;
}

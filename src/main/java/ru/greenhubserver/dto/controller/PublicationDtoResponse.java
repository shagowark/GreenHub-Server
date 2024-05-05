package ru.greenhubserver.dto.controller;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class PublicationDtoResponse {
    private String title;
    private String text;
    private byte[] image;
    private Set<String> tags;
    private Long rating;
    private Long commentsCount;
    private Long authorId;
    private byte[] authorImage;
    private String authorName;
}

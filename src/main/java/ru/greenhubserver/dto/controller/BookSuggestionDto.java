package ru.greenhubserver.dto.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookSuggestionDto {

    private String imageURL;

    private String isbn;

    private String isbn13;

    private String name;

    private Integer originalPublicationYear;

    private String originalTitle;

    private String smallImageURL;

    private String title;

    private String language;
}

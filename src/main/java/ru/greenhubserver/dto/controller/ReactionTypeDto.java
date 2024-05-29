package ru.greenhubserver.dto.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReactionTypeDto {
    @NotBlank(message = "reactionType mustn't be blank")
    private String reactionType;
}

package com.example.collaborativetools.card.Dto;

import com.example.collaborativetools.card.entitiy.Card;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.stream.Collectors;


@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardResponseDto {


    private final String title;
    private final String description;
    private final String backgroundColor;
    private final Integer sequence;
    private final LocalDateTime dueDate;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;
//    private List<CommentResponseDto> comments;

    public CardResponseDto(Card card) {
        this.title = card.getTitle();
        this.description = card.getDescription();
        this.backgroundColor = card.getBackgroundColor();
        this.sequence = card.getSequence();
        this.dueDate = card.getDueDate();
        this.createdAt = card.getCreatedAt();
        this.modifiedAt = card.getModifiedAt();
//        this.comments = card.getComments().stream()
//                .map(CommentResponseDto::new)
//                .collect(Collectors.toList());
    }
}

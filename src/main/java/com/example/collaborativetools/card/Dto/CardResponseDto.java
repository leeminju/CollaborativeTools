package com.example.collaborativetools.card.Dto;

import com.example.collaborativetools.card.entitiy.Card;
import com.example.collaborativetools.comment.dto.response.CommentResponse;
import com.example.collaborativetools.user.dto.UserInfoDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
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
    private final List<UserInfoDto>members;
    private final List<CommentResponse> comments;

    public CardResponseDto(Card card) {
        this.title = card.getTitle();
        this.description = card.getDescription();
        this.backgroundColor = card.getBackgroundColor();
        this.sequence = card.getSequence();
        this.dueDate = card.getDueDate();
        this.createdAt = card.getCreatedAt();
        this.modifiedAt = card.getModifiedAt();
        this.members = card.getUserCardList().stream()
                .map(userCard -> new UserInfoDto(userCard.getUser()))
                .collect(Collectors.toList());
        this.comments = card.getComments().stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }
}

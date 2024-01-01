package com.example.collaborativetools.board.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CardInColumnDTO(
        Long cardId, String cardTitle, String backgroundColor, LocalDateTime dueDate, Integer sequence
) {

}

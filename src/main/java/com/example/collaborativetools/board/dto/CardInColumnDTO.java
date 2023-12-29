package com.example.collaborativetools.board.dto;

import lombok.Builder;

@Builder
public record CardInColumnDTO(
    Long cardId, String cardTitle
) {

}

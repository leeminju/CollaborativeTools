package com.example.collaborativetools.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record InviteRequestBoardDTO(
    @NotBlank(message = "이름 공백일 수 없습니다.")
    String username) {

}

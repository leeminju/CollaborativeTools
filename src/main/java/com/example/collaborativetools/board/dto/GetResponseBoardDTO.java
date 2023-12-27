package com.example.collaborativetools.board.dto;

import com.example.collaborativetools.board.dto.CreateBoardDTO.Response;
import com.example.collaborativetools.board.entitiy.Board;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record GetResponseBoardDTO(
    Long id, String title, String desc, String backgroundColor, LocalDateTime modifiedAt
) {

  public static GetResponseBoardDTO of(Board board) {
    return GetResponseBoardDTO.builder()
        .id(board.getId())
        .title(board.getTitle())
        .backgroundColor(board.getBackgroundColor())
        .modifiedAt(board.getModifiedAt())
        .build();
  }

}

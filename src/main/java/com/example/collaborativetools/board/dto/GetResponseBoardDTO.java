package com.example.collaborativetools.board.dto;

import com.example.collaborativetools.board.dto.CreateBoardDTO.Response;
import com.example.collaborativetools.board.entitiy.Board;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record GetResponseBoardDTO(
    Long boardId, String title, String desc, String backgroundColor, LocalDateTime modifiedAt
) {

  public static GetResponseBoardDTO of(Board board) {
    return GetResponseBoardDTO.builder()
        .boardId(board.getId())
        .title(board.getTitle())
        .desc(board.getDescription())
        .backgroundColor(board.getBackgroundColor())
        .modifiedAt(board.getModifiedAt())
        .build();
  }

}

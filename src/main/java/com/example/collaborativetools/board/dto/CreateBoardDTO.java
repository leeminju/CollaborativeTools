package com.example.collaborativetools.board.dto;

import com.example.collaborativetools.board.entitiy.Board;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

public class CreateBoardDTO {

  public record Request(
      @NotBlank(message = "제목 공백일 수 없습니다.")
      String title,
      @NotBlank(message = "설명 공백일 수 없습니다.")
      String desc,

      String backgroundColor) {

    public Board toEntity() {
      return Board.builder()
          .title(title)
          .description(desc)
          .backgroundColor(backgroundColor)
          .build();
    }
  }

  @Builder
  public record Response(
      Long id, String title, String desc, String backgroundColor, LocalDateTime createdAt) {

    public static Response of(Board board) {
      return Response.builder()
          .id(board.getId())
          .title(board.getTitle())
          .backgroundColor(board.getBackgroundColor())
          .createdAt(board.getCreatedAt())
          .build();
    }
  }


}

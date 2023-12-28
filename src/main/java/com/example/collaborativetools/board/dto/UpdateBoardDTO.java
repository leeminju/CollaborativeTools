package com.example.collaborativetools.board.dto;

import com.example.collaborativetools.board.dto.CreateBoardDTO.Response;
import com.example.collaborativetools.board.entitiy.Board;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.Builder;

public class UpdateBoardDTO {

  public record Request(
      @NotBlank(message = "제목 공백일 수 없습니다.")
      String title,
      @NotBlank(message = "설명 공백일 수 없습니다.")
      String desc,

      String backgroundColor) {


  }

  @Builder
  public record Response(
      Long boardId, String title, String desc, String backgroundColor, LocalDateTime createdAt) {

    public static UpdateBoardDTO.Response of(Board board) {
      return UpdateBoardDTO.Response.builder()
          .boardId(board.getId())
          .title(board.getTitle())
          .desc(board.getDescription())
          .backgroundColor(board.getBackgroundColor())
          .createdAt(board.getCreatedAt())
          .build();
    }
  }


}

package com.example.collaborativetools.board.dto;

import com.example.collaborativetools.user.entitiy.User;
import lombok.Builder;

@Builder
public record GetMemberResponseBoardDTO(
    Long userId,
    String userName) {

  public static GetMemberResponseBoardDTO of(User user) {
    return GetMemberResponseBoardDTO.builder()
        .userId(user.getId())
        .userName(user.getUsername())
        .build();
  }

}

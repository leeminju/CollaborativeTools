package com.example.collaborativetools.board.dto;

import com.example.collaborativetools.global.constant.UserRoleEnum;
import com.example.collaborativetools.user.entitiy.User;
import com.example.collaborativetools.userboard.entity.UserBoard;
import lombok.Builder;

@Builder
public record GetMemberResponseBoardDTO(
        Long userId,
        String userName,
        UserRoleEnum role) {

    public static GetMemberResponseBoardDTO of(UserBoard userBoard) {
        return GetMemberResponseBoardDTO.builder()
                .userId(userBoard.getUser().getId())
                .userName(userBoard.getUser().getUsername())
                .role(userBoard.getRole())
                .build();
    }

}

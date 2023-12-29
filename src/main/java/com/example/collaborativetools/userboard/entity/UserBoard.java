package com.example.collaborativetools.userboard.entity;


import com.example.collaborativetools.board.entitiy.Board;
import com.example.collaborativetools.global.constant.UserRoleEnum;
import com.example.collaborativetools.global.constant.UserRoleEnum.Authority;
import com.example.collaborativetools.user.entitiy.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "user_boards")
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;

    public static UserBoard createUserBoard(Board board, User user, UserRoleEnum role) {
        return UserBoard.builder()
                .board(board)
                .user(user)
                .role(role)
                .build();
    }
}

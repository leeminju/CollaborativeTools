package com.example.collaborativetools.userboard.entity;


import com.example.collaborativetools.board.entitiy.Board;
import com.example.collaborativetools.global.constant.UserRoleEnum;
import com.example.collaborativetools.user.entitiy.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "user_boards")
public class UserBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;
}

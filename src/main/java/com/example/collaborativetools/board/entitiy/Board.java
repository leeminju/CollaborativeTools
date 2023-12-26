package com.example.collaborativetools.board.entitiy;

import com.example.collaborativetools.column.entitiy.Columns;
import com.example.collaborativetools.global.entity.Timestamped;
import com.example.collaborativetools.user.entitiy.User;
import com.example.collaborativetools.userboard.entity.UserBoard;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Builder
@Table(name = "boards")
public class Board extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String desc;

    @Column(nullable = false)
    private String backgroundColor;

    @OneToMany(mappedBy = "board")
    private List<UserBoard> userBoardList= new ArrayList<>();


    @OneToMany(mappedBy = "board")
    private List<Columns> columnsSet= new ArrayList<>();



}

package com.example.collaborativetools.board.entitiy;

import com.example.collaborativetools.column.entitiy.Columns;
import com.example.collaborativetools.global.entity.Timestamped;
import com.example.collaborativetools.userboard.entity.UserBoard;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Table(name = "boards")
@Builder
public class Board extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "background_color")
    private String backgroundColor;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<UserBoard> userBoardList = new ArrayList<>();


    @OneToMany(mappedBy = "board",cascade = CascadeType.REMOVE)
    private List<Columns> columnsList = new ArrayList<>();

    public void updateBoard(String title, String description, String backgroundColor) {
        this.title = title;
        this.description = description;
        this.backgroundColor = backgroundColor;
    }

}

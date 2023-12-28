package com.example.collaborativetools.column.entitiy;

import com.example.collaborativetools.board.entitiy.Board;
import com.example.collaborativetools.card.entitiy.Card;
import com.example.collaborativetools.global.entity.Timestamped;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "columns")
public class Columns extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Integer sequence;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @OneToMany(mappedBy = "column")
    @JsonIgnore
    private List<Card> cards;
}

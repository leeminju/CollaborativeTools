package com.example.collaborativetools.column.entitiy;

import com.example.collaborativetools.board.entitiy.Board;
import com.example.collaborativetools.card.entitiy.Card;
import com.example.collaborativetools.global.entity.Timestamped;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;

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

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "column",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    @JsonIgnore
    private Set<Card> cards = new LinkedHashSet<>();

    public Columns(String title, Integer sequence, Board board) {
        this.title = title;
        this.sequence = sequence;
        this.board = board;
    }

    public static Columns create(String title, Integer sequence, Board board) {
        return new Columns(title, sequence, board);
    }

    public void update(String title, Integer sequence, Board board) {
        this.title = title;
        this.sequence = sequence;
        this.board = board;
    }
}

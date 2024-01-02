package com.example.collaborativetools.column.entitiy;

import java.util.LinkedHashSet;
import java.util.Set;

import com.example.collaborativetools.board.entitiy.Board;
import com.example.collaborativetools.card.entitiy.Card;
import com.example.collaborativetools.column.dto.request.ColumnUpdateRequest;
import com.example.collaborativetools.global.entity.Timestamped;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Double sequence;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @OneToMany(
        fetch = FetchType.LAZY,
        mappedBy = "column",
        cascade = CascadeType.REMOVE,
        orphanRemoval = true
    )
    @OrderBy("sequence asc")
    @JsonIgnore
    private Set<Card> cards = new LinkedHashSet<>();

    private Columns(String title, Double sequence, Board board) {
        this.title = title;
        this.sequence = sequence;
        this.board = board;
    }

    public static Columns create(String title, Double sequence, Board board) {
        return new Columns(title, sequence, board);
    }

    public void update(ColumnUpdateRequest request) {
        this.title = request.getTitle();
        this.sequence = request.getSequence();
    }
}

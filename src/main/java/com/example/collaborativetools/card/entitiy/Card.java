package com.example.collaborativetools.card.entitiy;

import com.example.collaborativetools.column.entitiy.Columns;
import com.example.collaborativetools.comment.entitiy.Comment;
import com.example.collaborativetools.global.entity.Timestamped;
import com.example.collaborativetools.usercard.entity.UserCard;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter

public class Card extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "background_color",nullable = false)
    private String backgroundColor;

    @Column(nullable = false)
    private Integer sequence;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @ManyToOne
    @JoinColumn(name = "column_id", nullable = false)
    private Columns column;

    @OneToMany(mappedBy = "card")
    private List<Comment> comments;

    @OneToMany(mappedBy = "card")
    private List<UserCard> userCardList;
}

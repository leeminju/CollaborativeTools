package com.example.collaborativetools.card.entitiy;

import com.example.collaborativetools.card.Dto.CardRequestDto;
import com.example.collaborativetools.card.Dto.CardUpdateRequestDto;
import com.example.collaborativetools.column.entitiy.Columns;
import com.example.collaborativetools.comment.entitiy.Comment;
import com.example.collaborativetools.global.entity.Timestamped;
import com.example.collaborativetools.usercard.entity.UserCard;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Card extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String description;

    @Column(name = "background_color")
    private String backgroundColor;

    @Column
    private Integer sequence = 1;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @ManyToOne
    @JoinColumn(name = "column_id", nullable = false)
    @JsonIgnore
    private Columns column;

    @OneToMany(mappedBy = "card",cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private List<Comment> comments;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<UserCard> userCardList;

    public Card(CardRequestDto cardRequestDto) {
        this.title = cardRequestDto.getTitle();
    }

    public void update(CardUpdateRequestDto cardUpdateRequestDto) {
        this.title = cardUpdateRequestDto.getTitle();
        this.description = cardUpdateRequestDto.getDescription();
        this.backgroundColor = cardUpdateRequestDto.getBackgroundColor();
        this.dueDate = cardUpdateRequestDto.getDueDate();
    }
}

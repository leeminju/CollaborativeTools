package com.example.collaborativetools.comment.entitiy;

import com.example.collaborativetools.card.entitiy.Card;
import com.example.collaborativetools.comment.dto.request.CommentUpdateRequest;
import com.example.collaborativetools.global.entity.Timestamped;
import com.example.collaborativetools.user.entitiy.User;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "comments")
@Entity
public class Comment extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Card card;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private Comment(String comment, Card card, User user) {
        this.comment = comment;
        this.card = card;
        this.user = user;
    }

    public static Comment create(String comment, Card card, User user) {
        return new Comment(comment, card, user);
    }

    public void update(CommentUpdateRequest request) {
        this.comment = request.getComment();
    }
}

package com.example.collaborativetools.comment.entitiy;

import com.example.collaborativetools.card.entitiy.Card;
import com.example.collaborativetools.global.entity.Timestamped;
import com.example.collaborativetools.user.entitiy.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments")
@Entity
public class Comment extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "cards_id")
    private Card card;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private User user;


}

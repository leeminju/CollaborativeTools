package com.example.collaborativetools.usercard.entity;

import com.example.collaborativetools.card.entitiy.Card;
import com.example.collaborativetools.user.entitiy.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_cards")
public class UserCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public UserCard(User user, Card card) {
        this.user = user;
        this.card = card;
    }
}

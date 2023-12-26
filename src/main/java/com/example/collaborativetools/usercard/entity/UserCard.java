package com.example.collaborativetools.usercard.entity;

import com.example.collaborativetools.card.entitiy.Card;
import com.example.collaborativetools.user.entitiy.User;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "user_cards")
public class UserCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}

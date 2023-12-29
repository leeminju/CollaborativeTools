package com.example.collaborativetools.usercard.repository;

import com.example.collaborativetools.card.entitiy.Card;
import com.example.collaborativetools.user.entitiy.User;
import com.example.collaborativetools.usercard.entity.UserCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCardRepository extends JpaRepository<UserCard, Long> {
    boolean existsByCardAndUser(Card card, User user);
}

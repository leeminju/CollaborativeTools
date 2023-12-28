package com.example.collaborativetools.card.repository;

import com.example.collaborativetools.card.entitiy.Card;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface CardRepository extends JpaRepository<Card, Long> {

    @Query("SELECT MAX(c.sequence) FROM Card c WHERE c.column.id = :columnId")
    Integer findLastSequenceInColumn(@Param("columnId") Long columnId);

    List<Card> findBySequenceBetweenOrderBySequence(int start, int end);
}

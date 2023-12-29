package com.example.collaborativetools.userboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.collaborativetools.userboard.entity.UserBoard;

public interface UserBoardRepository extends JpaRepository<UserBoard,Long> {


  UserBoard findByUserIdAndBoardId(Long userId, Long boardId);

  boolean existsByBoardIdAndUserId(Long boardId, Long userId);


  List<UserBoard> findByUserId(Long userId);
}

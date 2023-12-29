package com.example.collaborativetools.userboard.repository;

import com.example.collaborativetools.board.entitiy.Board;
import com.example.collaborativetools.user.entitiy.User;
import com.example.collaborativetools.userboard.entity.UserBoard;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserBoardRepository extends JpaRepository<UserBoard,Long> {


  UserBoard findByUserIdAndBoardId(Long boardId, Long userId);
  Optional<UserBoard> findByBoardAndUser(Board board, User user);

  boolean existsByBoardIdAndUserId(Long boardId, Long userId);


  List<UserBoard> findByUserId(Long userId);
}

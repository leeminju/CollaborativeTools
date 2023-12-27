package com.example.collaborativetools.userboard.repository;

import com.example.collaborativetools.userboard.entity.UserBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserBoardRepository extends JpaRepository<UserBoard,Long> {


  UserBoard findByUserIdAndBoardId(Long boardId, Long userId);
}

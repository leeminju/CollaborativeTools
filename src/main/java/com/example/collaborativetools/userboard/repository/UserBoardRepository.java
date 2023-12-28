package com.example.collaborativetools.userboard.repository;

import com.example.collaborativetools.userboard.entity.UserBoard;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserBoardRepository extends JpaRepository<UserBoard,Long> {


  UserBoard findByUserIdAndBoardId(Long userId, Long boardId);


  List<UserBoard> findByUserId(Long userId);
}

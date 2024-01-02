package com.example.collaborativetools.board.repository;

import com.example.collaborativetools.board.entitiy.Board;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board,Long> {

  @Query("select b from Board As b join fetch b.columnsList WHERE b.id = :boardId")
  Board findBoardById(@Param("boardId") Long boardId);
}

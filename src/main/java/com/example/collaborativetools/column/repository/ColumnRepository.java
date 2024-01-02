package com.example.collaborativetools.column.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.collaborativetools.column.entitiy.Columns;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ColumnRepository extends JpaRepository<Columns, Long> {
  @Query("SELECT c FROM Columns As c LEFT JOIN FETCH c.cards WHERE c.id IN :columnList")
  List<Columns> findColumByIdList(@Param("columnList") List<Long> columnList);

  List<Columns> findAllByOrderBySequenceAsc();

}

package com.example.collaborativetools.column.repository;

import java.util.List;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.collaborativetools.column.entitiy.Columns;
import org.springframework.data.jpa.repository.Query;

public interface ColumnRepository extends JpaRepository<Columns, Long> {
    @Query("SELECT c FROM Columns As c LEFT JOIN FETCH c.cards WHERE c.id IN :columnList")
    List<Columns> findColumByIdList(@Param("columnList") List<Long> columnList);

}

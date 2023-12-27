package com.example.collaborativetools.column.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.collaborativetools.column.entitiy.Columns;

public interface ColumnRepository extends JpaRepository<Columns, Long> {
}

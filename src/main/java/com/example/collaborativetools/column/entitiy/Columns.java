package com.example.collaborativetools.column.entitiy;

import com.example.collaborativetools.global.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "columns")
@Entity
public class Columns extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "order", nullable = false)
    private Integer order;
}

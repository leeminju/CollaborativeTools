package com.example.collaborativetools.card.entitiy;

import com.example.collaborativetools.column.entitiy.Columns;
import com.example.collaborativetools.comment.entitiy.Comment;
import com.example.collaborativetools.global.entity.Timestamped;
import com.example.collaborativetools.usercard.entity.UserCard;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Getter
@Table(name = "cards")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Card extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String desc;

    @Column(nullable = false)
    private String background_color;

    private int order;
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Columns column;

    @OneToMany(mappedBy = "comment")
    private List<Comment> comments;

    @OneToMany(mappedBy = "usercard")
    private List<UserCard> userCardList;
}

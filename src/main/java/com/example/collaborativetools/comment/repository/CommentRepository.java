package com.example.collaborativetools.comment.repository;

import com.example.collaborativetools.user.entitiy.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.collaborativetools.comment.entitiy.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    void deleteAllByUser(User user);
}

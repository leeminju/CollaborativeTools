package com.example.collaborativetools.user.repository;

import com.example.collaborativetools.user.entitiy.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<Object> findByUsername(String username);
}

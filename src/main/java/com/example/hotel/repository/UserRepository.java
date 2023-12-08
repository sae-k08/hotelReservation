package com.example.hotel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hotel.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
     public User findByEmail(String email);
     public Page<User> findByNameLikeOrFuriganaLike(String nameKeyword, String furiganaKeyword, Pageable pageable);
     
} 

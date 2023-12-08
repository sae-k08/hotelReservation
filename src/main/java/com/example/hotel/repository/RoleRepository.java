package com.example.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hotel.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
     public Role findByName(String name);
}

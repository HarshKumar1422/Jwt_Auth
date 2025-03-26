package com.example.jwtauthdemo.repository;

import com.example.jwtauthdemo.model.ERole;
import com.example.jwtauthdemo.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
}

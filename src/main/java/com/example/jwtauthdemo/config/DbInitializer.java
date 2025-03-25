package com.example.jwtauthdemo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.jwtauthdemo.model.ERole;
import com.example.jwtauthdemo.model.Role;
import com.example.jwtauthdemo.repository.RoleRepository;

@Configuration
public class DbInitializer {
    @Bean
    CommandLineRunner initDatabase(RoleRepository roleRepository) {
        return args -> {
            // Initialize roles if they don't exist
            if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
                Role userRole = new Role();
                userRole.setName(ERole.ROLE_USER);
                roleRepository.save(userRole);
            }

            if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
                Role adminRole = new Role();
                adminRole.setName(ERole.ROLE_ADMIN);
                roleRepository.save(adminRole);
            }
        };
    }
}

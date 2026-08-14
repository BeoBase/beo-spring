package com.beobase.beospring.user.internal;

import org.springframework.data.jpa.repository.JpaRepository;

interface UserRepository extends JpaRepository<User, String> {
    // JpaRepository provides basic CRUD operations

    // Check if a user exists by email
    boolean existsByEmailIgnoreCase(String email);
}

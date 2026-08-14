package com.beobase.beospring.user.internal;

import org.springframework.data.jpa.repository.JpaRepository;

interface UserRepository extends JpaRepository<User, String> {
    // JpaRepository provides basic CRUD operations
}

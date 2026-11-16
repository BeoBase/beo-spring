package com.beobase.beospring.app.internal;

import org.springframework.data.jpa.repository.JpaRepository;

interface AppRepository extends JpaRepository<App, String> {
    // JpaRepository provides basic CRUD operations
}

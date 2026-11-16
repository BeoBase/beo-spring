package com.beobase.beospring.app.internal;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

interface AppRepository extends JpaRepository<App, String> {
    // JpaRepository provides basic CRUD operations

    // App names are unique ignoring case (see the apps_name_unique index)
    boolean existsByNameIgnoreCase(String name);

    // Find an app by its name, ignoring case
    Optional<App> findByNameIgnoreCase(String name);
}

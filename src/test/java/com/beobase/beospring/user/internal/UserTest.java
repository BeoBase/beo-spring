package com.beobase.beospring.user.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

public class UserTest {

    @Test
    void constructor_shouldInitializeUser() {
        String name = "John Doe";
        String email = "john@example.com";
        String passwordHashed = "hashed-password";
        Role role = Role.ROLE_USER;

        User user = new User(name, email, passwordHashed, role);

        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(passwordHashed, user.getPasswordHashed());
        assertEquals(role, user.getRole());
        assertNull(user.getId());
    }

    @Test
    void constructor_shouldNormalizeEmail() {
        User user = new User(
                "John Doe",
                "  JOHN@EXAMPLE.COM  ",
                "hashed-password",
                Role.ROLE_USER
        );

        assertEquals("john@example.com", user.getEmail());
    }

    @Test
    void isActive_shouldDefaultToTrue() {
        User user = new User();
        assertTrue(user.isActive());
    }

    @Test
    void createdAt_shouldBeInitialized() {
        Instant before = Instant.now();
        User user = new User();
        Instant after = Instant.now();

        assertNotNull(user.getCreatedAt());
        assertFalse(user.getCreatedAt().isBefore(before));
        assertFalse(user.getCreatedAt().isAfter(after));
    }

    @Test
    void setEmail_shouldTrimAndLowercaseEmail() {
        User user = new User();
        user.setEmail("  JOHN.DOE@EXAMPLE.COM  ");
        assertEquals("john.doe@example.com", user.getEmail());
    }

    @Test
    void setEmail_shouldAllowNull() {
        User user = new User();
        user.setEmail(null);
        assertNull(user.getEmail());
    }

    @Test
    void setEmail_shouldPreserveAlreadyNormalizedEmail() {
        User user = new User();
        user.setEmail("john@example.com");
        assertEquals("john@example.com", user.getEmail());
    }

    @Test
    void setters_shouldUpdateFields() {
        User user = new User();
        user.setId("USR001");
        user.setName("John Doe");
        user.setPasswordHashed("new-hash");
        user.setRole(Role.ROLE_ADMIN);
        user.setActive(false);

        assertEquals("USR001", user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("new-hash", user.getPasswordHashed());
        assertEquals(Role.ROLE_ADMIN, user.getRole());
        assertFalse(user.isActive());
    }
}

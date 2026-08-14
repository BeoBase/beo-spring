package com.beobase.beospring.user.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void constructorShouldInitializeDefaultValues() {
        User user = new User();

        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getPasswordHashed());
        assertNull(user.getRole());

        assertTrue(user.isActive());
        assertNotNull(user.getCreatedAt());
    }

    @Test
    void isActiveShouldDefaultToTrue() {
        User user = new User();
        assertTrue(user.isActive());
    }

    @Test
    void setActiveShouldUpdateActiveStatus() {
        User user = new User();
        user.setActive(false);
        assertFalse(user.isActive());
    }

    @Test
    void createdAtShouldBeInitialized() {
        Instant before = Instant.now();
        User user = new User();
        Instant after = Instant.now();

        assertNotNull(user.getCreatedAt());
        assertFalse(user.getCreatedAt().isBefore(before));
        assertFalse(user.getCreatedAt().isAfter(after));
    }

    @Test
    void setEmailShouldTrimAndLowercaseEmail() {
        User user = new User();
        user.setEmail("  JOHN.DOE@EXAMPLE.COM  ");
        assertEquals("john.doe@example.com", user.getEmail());
    }

    @Test
    void setEmailShouldAllowNull() {
        User user = new User();
        user.setEmail(null);
        assertNull(user.getEmail());
    }

    @Test
    void setEmailShouldPreserveAlreadyNormalizedEmail() {
        User user = new User();
        user.setEmail("john@example.com");
        assertEquals("john@example.com", user.getEmail());
    }

    @Test
    void setEmailShouldNormalizeMixedCaseEmail() {
        User user = new User();

        user.setEmail("John.Doe@Example.COM");

        assertEquals("john.doe@example.com", user.getEmail());
    }

    @Test
    void setEmailShouldTrimLeadingAndTrailingWhitespace() {
        User user = new User();

        user.setEmail("   john@example.com   ");

        assertEquals("john@example.com", user.getEmail());
    }

    @Test
    void setEmailShouldUseRootLocale() {
        User user = new User();

        user.setEmail("I@EXAMPLE.COM");

        assertEquals("i@example.com", user.getEmail());
    }

    @Test
    void settersShouldUpdateFields() {
        User user = new User();

        user.setId("USR001");
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPasswordHashed("new-hash");
        user.setRole(Role.ROLE_ADMIN);
        user.setActive(false);

        assertEquals("USR001", user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("new-hash", user.getPasswordHashed());
        assertEquals(Role.ROLE_ADMIN, user.getRole());
        assertFalse(user.isActive());
    }
}

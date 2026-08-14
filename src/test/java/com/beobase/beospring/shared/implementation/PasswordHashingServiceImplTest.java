package com.beobase.beospring.shared.implementation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class PasswordHashingServiceImplTest {

    private final PasswordHashingServiceImpl passwordService = new PasswordHashingServiceImpl();

    @Test
    void hashShouldReturnValidBcryptHash() {
        String rawPassword = "myPassword123";
        String hashedPassword = passwordService.hash(rawPassword);
        assertNotNull(hashedPassword);
        assertNotEquals(rawPassword, hashedPassword);
        assertTrue(hashedPassword.startsWith("$2"));
    }

    @Test
    void matchesShouldReturnTrueWhenPasswordIsCorrect() {
        String rawPassword = "myPassword123";
        String hashedPassword = passwordService.hash(rawPassword);
        boolean result = passwordService.matches(rawPassword, hashedPassword);
        assertTrue(result);
    }

    @Test
    void matchesShouldReturnFalseWhenPasswordIsIncorrect() {
        String rawPassword = "myPassword123";
        String wrongPassword = "wrongPassword";
        String hashedPassword = passwordService.hash(rawPassword);
        boolean result = passwordService.matches(wrongPassword, hashedPassword);
        assertFalse(result);
    }

    @Test
    void hashShouldProduceDifferentHashesForSamePassword() {
        String rawPassword = "myPassword123";
        String firstHash = passwordService.hash(rawPassword);
        String secondHash = passwordService.hash(rawPassword);
        assertNotEquals(firstHash, secondHash);
        assertTrue(passwordService.matches(rawPassword, firstHash));
        assertTrue(passwordService.matches(rawPassword, secondHash));
    }
}

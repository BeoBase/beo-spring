package com.beobase.beospring.user.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RoleTest {

    @Test
    void shouldContainAllExpectedRoles() {
        assertEquals(4, Role.values().length);

        assertEquals(Role.ROLE_ADMIN, Role.valueOf("ROLE_ADMIN"));
        assertEquals(Role.ROLE_SUPPORT, Role.valueOf("ROLE_SUPPORT"));
        assertEquals(Role.ROLE_USER, Role.valueOf("ROLE_USER"));
        assertEquals(Role.ROLE_DEMO, Role.valueOf("ROLE_DEMO"));
    }
}

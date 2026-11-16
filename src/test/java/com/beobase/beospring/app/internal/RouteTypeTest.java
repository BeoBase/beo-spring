package com.beobase.beospring.app.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RouteTypeTest {

    @Test
    void shouldContainAllExpectedRouteTypes() {
        assertEquals(2, RouteType.values().length);

        assertEquals(RouteType.INTERNAL, RouteType.valueOf("INTERNAL"));
        assertEquals(RouteType.EXTERNAL, RouteType.valueOf("EXTERNAL"));
    }
}

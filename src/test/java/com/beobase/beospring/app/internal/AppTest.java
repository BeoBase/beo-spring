package com.beobase.beospring.app.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class AppTest {

    @Test
    void constructorShouldStartEmptyWithAnEmptyTagList() {
        App app = new App();

        assertNull(app.getId());
        assertNull(app.getName());
        assertNull(app.getRoute());
        assertNull(app.getRouteType());

        assertNotNull(app.getTags());
        assertTrue(app.getTags().isEmpty());
    }

    @Test
    void settersShouldUpdateAllFields() {
        App app = new App();

        app.setId("aB3xK9pQ2m");
        app.setName("Bank");
        app.setTags(List.of("Cashflow Tracking", "Assets"));
        app.setRoute("bank");
        app.setRouteType(RouteType.INTERNAL);

        assertEquals("aB3xK9pQ2m", app.getId());
        assertEquals("Bank", app.getName());
        assertEquals(List.of("Cashflow Tracking", "Assets"), app.getTags());
        assertEquals("bank", app.getRoute());
        assertEquals(RouteType.INTERNAL, app.getRouteType());
    }
}

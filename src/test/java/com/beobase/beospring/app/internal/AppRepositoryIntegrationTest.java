package com.beobase.beospring.app.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// Runs against the in-memory H2 database, so it checks the entity mapping (schema, tags array, enum)
@SpringBootTest
@ActiveProfiles("test")
class AppRepositoryIntegrationTest {

    @Autowired
    private AppRepository appRepository;

    @BeforeEach
    void setUp() {
        appRepository.deleteAll();
    }

    private App newApp(String id, String name, List<String> tags, String route, RouteType type) {
        App app = new App();
        app.setId(id);
        app.setName(name);
        app.setTags(tags);
        app.setRoute(route);
        app.setRouteType(type);
        return app;
    }

    @Test
    void shouldStartEmpty() {
        assertTrue(appRepository.findAll().isEmpty());
    }

    @Test
    void shouldSaveAndLoadAnAppWithSeveralTags() {
        appRepository.saveAndFlush(newApp(
                "aB3xK9pQ2m", "Bank", List.of("Cashflow Tracking", "Assets"), "bank", RouteType.INTERNAL));

        App loaded = appRepository.findById("aB3xK9pQ2m").orElseThrow();

        assertEquals("Bank", loaded.getName());
        assertEquals(List.of("Cashflow Tracking", "Assets"), loaded.getTags());
        assertEquals("bank", loaded.getRoute());
        assertEquals(RouteType.INTERNAL, loaded.getRouteType());
    }

    @Test
    void shouldSaveAnExternalApp() {
        appRepository.saveAndFlush(newApp(
                "xY7mN2kL9q", "Jira Board", List.of("Developments"),
                "https://example.com/board", RouteType.EXTERNAL));

        App loaded = appRepository.findById("xY7mN2kL9q").orElseThrow();

        assertEquals(RouteType.EXTERNAL, loaded.getRouteType());
        assertEquals("https://example.com/board", loaded.getRoute());
    }

    @Test
    void shouldAllowSeveralAppsWithTheSameName() {
        appRepository.saveAndFlush(newApp("id00000001", "Home", List.of("A"), "a", RouteType.INTERNAL));
        appRepository.saveAndFlush(newApp("id00000002", "Home", List.of("B"), "b", RouteType.INTERNAL));

        assertEquals(2, appRepository.count());
    }
}

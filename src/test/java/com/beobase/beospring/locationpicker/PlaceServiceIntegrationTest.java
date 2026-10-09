package com.beobase.beospring.locationpicker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// Runs against the in-memory H2 database, so it checks the entity mapping and the real catalog
@SpringBootTest
@ActiveProfiles("test")
class PlaceServiceIntegrationTest {

    @Autowired
    private PlaceService placeService;

    @BeforeEach
    void setUp() {
        placeService.saveUserPlaces(List.of());
    }

    @Test
    void shouldStartWithAnEmptyPickedList() {
        assertEquals(List.of(), placeService.findUserPlaces());
    }

    @Test
    void shouldPersistThePickedPlacesInOrder() {
        placeService.saveUserPlaces(List.of("p3", "p1"));

        assertEquals(
                List.of("p3", "p1"),
                placeService.findUserPlaces().stream().map(PlaceInfo::id).toList()
        );
    }

    @Test
    void shouldReplaceThePreviousPickedList() {
        placeService.saveUserPlaces(List.of("p1", "p2"));
        placeService.saveUserPlaces(List.of("p2"));

        assertEquals(
                List.of("p2"),
                placeService.findUserPlaces().stream().map(PlaceInfo::id).toList()
        );
    }

    @Test
    void shouldRejectUnknownPlaces() {
        assertThrows(IllegalArgumentException.class, () -> placeService.saveUserPlaces(List.of("nope")));
    }

    @Test
    void shouldServeTheWholeCatalog() {
        assertEquals(18, placeService.findAllPlaces().size());
    }
}

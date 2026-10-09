package com.beobase.beospring.locationpicker.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.beobase.beospring.locationpicker.PlaceInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class PlaceCatalogTest {

    private PlaceCatalog placeCatalog;

    @BeforeEach
    void setUp() {
        placeCatalog = new PlaceCatalog(JsonMapper.builder().build());
    }

    @Test
    void shouldLoadAllPlacesFromResource() {
        assertEquals(18, placeCatalog.findAll().size());
    }

    @Test
    void shouldMapAllFields() {
        PlaceInfo place = placeCatalog.findAll().getFirst();

        assertEquals("p1", place.id());
        assertEquals("Forest Waterfall", place.title());
        assertEquals("forest-waterfall.jpg", place.image().src());
        assertEquals(44.5588, place.lat());
        assertEquals(-80.344, place.lon());
    }

    @Test
    void shouldFindPlaceById() {
        assertEquals("Forest Waterfall", placeCatalog.findById("p1").orElseThrow().title());
    }

    @Test
    void shouldReturnEmptyForUnknownId() {
        assertTrue(placeCatalog.findById("nope").isEmpty());
    }
}

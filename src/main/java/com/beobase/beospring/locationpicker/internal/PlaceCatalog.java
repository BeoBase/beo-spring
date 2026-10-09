package com.beobase.beospring.locationpicker.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;

import com.beobase.beospring.locationpicker.PlaceInfo;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

// Read-only list of places, loaded once from the places.json resource
@Component
class PlaceCatalog {

    private static final String PLACES_RESOURCE = "location-picker/places.json";

    private final List<PlaceInfo> places;

    PlaceCatalog(ObjectMapper objectMapper) {
        try (InputStream in = new ClassPathResource(PLACES_RESOURCE).getInputStream()) {
            this.places = List.copyOf(objectMapper.readValue(in, new TypeReference<List<PlaceInfo>>() { }));
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load " + PLACES_RESOURCE, e);
        }
    }

    List<PlaceInfo> findAll() {
        return places;
    }

    Optional<PlaceInfo> findById(String id) {
        return places.stream()
                .filter(place -> place.id().equals(id))
                .findFirst();
    }
}

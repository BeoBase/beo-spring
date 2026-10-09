package com.beobase.beospring.locationpicker.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.beobase.beospring.locationpicker.PlaceInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaceServiceImplTest {

    private static final PlaceInfo P1 = new PlaceInfo("p1", "One", new PlaceInfo.Image("1.jpg", "one"), 1, 2);
    private static final PlaceInfo P2 = new PlaceInfo("p2", "Two", new PlaceInfo.Image("2.jpg", "two"), 3, 4);

    @Mock
    private PlaceCatalog placeCatalog;

    private PlaceServiceImpl placeService;

    @BeforeEach
    void setUp() {
        placeService = new PlaceServiceImpl(placeCatalog);
    }

    @Test
    void findAllPlacesShouldReturnTheCatalog() {
        when(placeCatalog.findAll()).thenReturn(List.of(P1, P2));

        assertEquals(List.of(P1, P2), placeService.findAllPlaces());
    }

    @Test
    void findUserPlacesShouldStartEmpty() {
        assertEquals(List.of(), placeService.findUserPlaces());
    }

    @Test
    void saveUserPlacesShouldKeepTheOrderAndDropDuplicates() {
        when(placeCatalog.findById("p2")).thenReturn(Optional.of(P2));
        when(placeCatalog.findById("p1")).thenReturn(Optional.of(P1));

        List<PlaceInfo> saved = placeService.saveUserPlaces(List.of("p2", "p1", "p2"));

        assertEquals(List.of(P2, P1), saved);
        assertEquals(List.of(P2, P1), placeService.findUserPlaces());
    }

    @Test
    void saveUserPlacesShouldReplaceThePreviousList() {
        when(placeCatalog.findById("p1")).thenReturn(Optional.of(P1));
        when(placeCatalog.findById("p2")).thenReturn(Optional.of(P2));

        placeService.saveUserPlaces(List.of("p1", "p2"));
        placeService.saveUserPlaces(List.of("p2"));

        assertEquals(List.of(P2), placeService.findUserPlaces());
    }

    @Test
    void saveUserPlacesShouldAcceptAnEmptyList() {
        when(placeCatalog.findById("p1")).thenReturn(Optional.of(P1));
        placeService.saveUserPlaces(List.of("p1"));

        assertEquals(List.of(), placeService.saveUserPlaces(List.of()));
        assertEquals(List.of(), placeService.findUserPlaces());
    }

    @Test
    void saveUserPlacesShouldRejectUnknownIdsAndKeepTheOldList() {
        when(placeCatalog.findById("p1")).thenReturn(Optional.of(P1));
        when(placeCatalog.findById("nope")).thenReturn(Optional.empty());
        placeService.saveUserPlaces(List.of("p1"));

        assertThrows(IllegalArgumentException.class, () -> placeService.saveUserPlaces(List.of("nope")));

        assertEquals(List.of(P1), placeService.findUserPlaces());
    }

    @Test
    void saveUserPlacesShouldRejectNull() {
        assertThrows(IllegalArgumentException.class, () -> placeService.saveUserPlaces(null));
    }
}

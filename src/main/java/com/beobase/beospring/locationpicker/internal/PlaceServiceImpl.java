package com.beobase.beospring.locationpicker.internal;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import com.beobase.beospring.locationpicker.PlaceInfo;
import com.beobase.beospring.locationpicker.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class PlaceServiceImpl implements PlaceService {

    private final PlaceCatalog placeCatalog;

    // TODO: kept in memory for now (one shared list, reset on restart).
    //  Persist it in a table, scoped to the logged-in user, later.
    private final AtomicReference<List<String>> userPlaceIds = new AtomicReference<>(List.of());

    @Override
    public List<PlaceInfo> findAllPlaces() {
        return placeCatalog.findAll();
    }

    @Override
    public List<PlaceInfo> findUserPlaces() {
        return userPlaceIds.get().stream()
                .map(placeCatalog::findById)
                .flatMap(Optional::stream)
                .toList();
    }

    @Override
    public List<PlaceInfo> saveUserPlaces(List<String> placeIds) {
        if (placeIds == null) {
            throw new IllegalArgumentException("placeIds must not be null");
        }

        List<PlaceInfo> places = placeIds.stream()
                .distinct()
                .map(id -> placeCatalog.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Unknown place id: " + id)))
                .toList();

        userPlaceIds.set(places.stream().map(PlaceInfo::id).toList());

        return places;
    }
}

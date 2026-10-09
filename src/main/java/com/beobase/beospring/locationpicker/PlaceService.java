package com.beobase.beospring.locationpicker;

import java.util.List;

public interface PlaceService {

    List<PlaceInfo> findAllPlaces();

    // TODO: scope the picked places to the logged-in user once the location picker is secured
    List<PlaceInfo> findUserPlaces();

    // Replaces the whole picked list, keeping the given order. Throws IllegalArgumentException on unknown ids.
    List<PlaceInfo> saveUserPlaces(List<String> placeIds);
}

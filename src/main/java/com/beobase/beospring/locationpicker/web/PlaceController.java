package com.beobase.beospring.locationpicker.web;

import com.beobase.beospring.locationpicker.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// TODO: these endpoints are public for now (see SecurityConfig), secure them with the USER role later
@RestController
@RequestMapping("/location-picker")
@RequiredArgsConstructor
class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/places")
    PlacesResponse getPlaces() {
        return new PlacesResponse(placeService.findAllPlaces());
    }

    @GetMapping("/user-places")
    PlacesResponse getUserPlaces() {
        return new PlacesResponse(placeService.findUserPlaces());
    }

    @PutMapping("/user-places")
    ResponseEntity<PlacesResponse> updateUserPlaces(@RequestBody UserPlacesRequest request) {
        try {
            return ResponseEntity.ok(new PlacesResponse(placeService.saveUserPlaces(request.placeIds())));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}

package com.beobase.beospring.locationpicker.web;

import java.util.List;

import com.beobase.beospring.locationpicker.PlaceInfo;

public record PlacesResponse(List<PlaceInfo> places) {
}

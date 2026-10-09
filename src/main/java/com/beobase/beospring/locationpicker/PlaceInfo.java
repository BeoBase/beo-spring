package com.beobase.beospring.locationpicker;

public record PlaceInfo(String id, String title, Image image, double lat, double lon) {

    public record Image(String src, String alt) {
    }
}

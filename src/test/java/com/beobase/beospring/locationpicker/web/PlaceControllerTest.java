package com.beobase.beospring.locationpicker.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.beobase.beospring.locationpicker.PlaceInfo;
import com.beobase.beospring.locationpicker.PlaceService;
import com.beobase.beospring.shared.TokenService;
import com.beobase.beospring.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PlaceController.class)
class PlaceControllerTest {

    private static final PlaceInfo P1 = new PlaceInfo(
            "p1", "Forest Waterfall", new PlaceInfo.Image("forest-waterfall.jpg", "A waterfall"), 44.5, -80.3);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlaceService placeService;

    // Satisfy TokenAuthenticationFilter's constructor dependencies
    @MockitoBean
    private TokenService tokenService;
    @MockitoBean
    private UserService userService;

    @Test
    void getPlacesShouldReturnThePlacesWrapper() throws Exception {
        when(placeService.findAllPlaces()).thenReturn(List.of(P1));

        mockMvc.perform(get("/location-picker/places"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.places[0].id").value("p1"))
                .andExpect(jsonPath("$.places[0].title").value("Forest Waterfall"))
                .andExpect(jsonPath("$.places[0].image.src").value("forest-waterfall.jpg"))
                .andExpect(jsonPath("$.places[0].lat").value(44.5));
    }

    @Test
    void getUserPlacesShouldReturnThePickedPlaces() throws Exception {
        when(placeService.findUserPlaces()).thenReturn(List.of(P1));

        mockMvc.perform(get("/location-picker/user-places"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.places[0].id").value("p1"));
    }

    @Test
    void putUserPlacesShouldSaveAndReturnThePickedPlaces() throws Exception {
        when(placeService.saveUserPlaces(List.of("p1"))).thenReturn(List.of(P1));

        mockMvc.perform(put("/location-picker/user-places")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"placeIds\": [\"p1\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.places[0].id").value("p1"));
    }

    @Test
    void putUserPlacesShouldReturnBadRequestForUnknownIds() throws Exception {
        when(placeService.saveUserPlaces(List.of("nope"))).thenThrow(new IllegalArgumentException("Unknown"));

        mockMvc.perform(put("/location-picker/user-places")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"placeIds\": [\"nope\"]}"))
                .andExpect(status().isBadRequest());
    }
}

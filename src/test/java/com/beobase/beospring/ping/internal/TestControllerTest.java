package com.beobase.beospring.ping.internal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
public class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnTestMessage() throws Exception {
        mockMvc.perform(get("/test/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ok message from Beo Spring"));
    }

    @Test
    void shouldAllowLocalhostFrontend() throws Exception {
        mockMvc.perform(get("/test/test")
                        .header("Origin", "http://localhost:5173"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Access-Control-Allow-Origin",
                        "http://localhost:5173"
                ));
    }

    @Test
    void shouldAllowBeoBaseDomain() throws Exception {
        mockMvc.perform(get("/test/test")
                        .header("Origin", "https://beobase.com"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Access-Control-Allow-Origin",
                        "https://beobase.com"
                ));
    }

    @Test
    void shouldAllowWwwBeoBaseDomain() throws Exception {
        mockMvc.perform(get("/test/test")
                        .header("Origin", "https://www.beobase.com"))
                .andExpect(status().isOk())
                .andExpect(header().string(
                        "Access-Control-Allow-Origin",
                        "https://www.beobase.com"
                ));
    }

    @Test
    void shouldNotAllowUnknownOrigin() throws Exception {
        mockMvc.perform(get("/test/test")
                        .header("Origin", "https://example.com"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist(
                        "Access-Control-Allow-Origin"
                ));
    }
}

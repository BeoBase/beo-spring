package com.beobase.beospring.ping.internal;

import com.beobase.beospring.shared.TokenService;
import com.beobase.beospring.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Provides a mock TokenService and UserService to satisfy TokenAuthenticationFilter's constructor dependency
    @MockitoBean
    private TokenService tokenService;
    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("GET /test/test should return 200 OK and expected message")
    void shouldReturnOkMessageWhenTestEndpointIsCalled() throws Exception {
        mockMvc.perform(get("/test/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("Ok message from Beo Spring"));
    }
}

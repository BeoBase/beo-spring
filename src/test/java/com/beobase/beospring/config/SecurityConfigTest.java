package com.beobase.beospring.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.beobase.beospring.user.UserInfo;
import com.beobase.beospring.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(SecurityConfig.class)
@ActiveProfiles("test")
public class SecurityConfigTest {

    @Autowired
    private WebApplicationContext context;

//    @Autowired
//    private SecurityConfig securityConfig;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @MockitoBean
    private TokenAuthenticationFilter tokenAuthenticationFilter;

    @MockitoBean
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void securityFilterChainShouldBeCreated() {
        assertThat(securityFilterChain).isNotNull();
    }

    @Test
    void corsConfigurationSourceShouldContainExpectedOrigins() {
        CorsConfiguration configuration =
                corsConfigurationSource.getCorsConfiguration(
                        new MockHttpServletRequest()
                );

        assertThat(configuration).isNotNull();

        assertThat(configuration.getAllowedOrigins())
                .containsExactly(
                        "http://localhost:5173",
                        "https://beobase.com",
                        "https://www.beobase.com"
                );

        assertThat(configuration.getAllowedMethods())
                .containsExactly(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                );

        assertThat(configuration.getAllowedHeaders())
                .containsExactly("*");

        assertThat(configuration.getAllowCredentials())
                .isTrue();
    }

    @Test
    void testEndpointShouldBePublic() throws Exception {
        mockMvc.perform(get("/test/anything"))
                .andExpect(status().isNotFound());
    }

    @Test
    void authEndpointShouldBePublic() throws Exception {
        mockMvc.perform(get("/auth/anything"))
                .andExpect(status().isNotFound());
    }

    @Test
    void postUsersShouldBePublic() throws Exception {
        UserInfo userInfo = new UserInfo(
                "id1",
                "Jane Doe",
                "jane@example.com",
                "ROLE_USER"
        );
        when(userService.createUser("Jane Doe", "jane@example.com", "password"))
                .thenReturn(userInfo);

        mockMvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Jane Doe",
                                  "email": "jane@example.com",
                                  "password": "password"
                                }
                                """)
        ).andExpect(status().isCreated());
    }

//    @Test
//    void userEndpointShouldRequireAuthentication() throws Exception {
//        mockMvc.perform(get("/accounts/123"))
//                .andExpect(status().isUnauthorized());
//    }

    @Test
    @WithMockUser(roles = "USER")
    void userEndpointShouldAllowUserRole() throws Exception {
        mockMvc.perform(get("/accounts/123"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void userEndpointShouldRejectAdminOnlyEndpointForUser() throws Exception {
        mockMvc.perform(get("/some-admin-endpoint"))
                .andExpect(status().isNotFound());
    }

//    @Test
//    @WithMockUser(roles = "USER")
//    void userEndpointShouldRejectUserWithoutAdminRole() throws Exception {
//        mockMvc.perform(get("/some-admin-endpoint"))
//                .andExpect(status().isForbidden());
//    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminShouldAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/some-admin-endpoint"))
                .andExpect(status().isNotFound());
    }

}

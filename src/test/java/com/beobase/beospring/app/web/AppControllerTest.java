package com.beobase.beospring.app.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.beobase.beospring.app.AppInfo;
import com.beobase.beospring.app.AppService;
import com.beobase.beospring.shared.TokenService;
import com.beobase.beospring.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AppController.class)
class AppControllerTest {

    private static final AppInfo BANK = new AppInfo(
            "aB3xK9pQ2m", "Bank", List.of("Cashflow Tracking", "Assets"), "bank", "INTERNAL");
    private static final AppInfo JIRA = new AppInfo(
            "xY7mN2kL9q", "Jira Board", List.of("Developments"), "https://example.com", "EXTERNAL");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppService appService;

    // Satisfy TokenAuthenticationFilter's constructor dependencies
    @MockitoBean
    private TokenService tokenService;
    @MockitoBean
    private UserService userService;

    @Test
    void getAppsShouldReturnTheAppsWrapper() throws Exception {
        when(appService.findAllApps()).thenReturn(List.of(BANK, JIRA));

        mockMvc.perform(get("/apps"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apps.length()").value(2))
                .andExpect(jsonPath("$.apps[0].id").value("aB3xK9pQ2m"))
                .andExpect(jsonPath("$.apps[0].name").value("Bank"))
                .andExpect(jsonPath("$.apps[0].tags[0]").value("Cashflow Tracking"))
                .andExpect(jsonPath("$.apps[0].tags[1]").value("Assets"))
                .andExpect(jsonPath("$.apps[0].route").value("bank"))
                .andExpect(jsonPath("$.apps[0].routeType").value("INTERNAL"))
                .andExpect(jsonPath("$.apps[1].routeType").value("EXTERNAL"));
    }

    @Test
    void getAppsShouldReturnAnEmptyListWhenThereAreNoApps() throws Exception {
        when(appService.findAllApps()).thenReturn(List.of());

        mockMvc.perform(get("/apps"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apps.length()").value(0));
    }

    @Test
    void getAppsShouldBePublic() throws Exception {
        // No login, no token: the home page reads this list
        when(appService.findAllApps()).thenReturn(List.of());

        mockMvc.perform(get("/apps"))
                .andExpect(status().isOk());
    }

    @Test
    void postAppsShouldNotBeAllowed() throws Exception {
        // Only GET is public and only GET exists
        mockMvc.perform(post("/apps"))
                .andExpect(status().is4xxClientError());
    }
}

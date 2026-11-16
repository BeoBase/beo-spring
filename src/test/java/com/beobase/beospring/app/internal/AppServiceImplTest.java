package com.beobase.beospring.app.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.beobase.beospring.app.AppInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class AppServiceImplTest {

    @Mock
    private AppRepository appRepository;

    private AppServiceImpl appService;

    @BeforeEach
    void setUp() {
        appService = new AppServiceImpl(appRepository);
    }

    private App newApp(String id, String name, List<String> tags, String route, RouteType type) {
        App app = new App();
        app.setId(id);
        app.setName(name);
        app.setTags(tags);
        app.setRoute(route);
        app.setRouteType(type);
        return app;
    }

    @Test
    void findAllAppsShouldMapEveryFieldToAppInfo() {
        when(appRepository.findAll(Sort.by("name"))).thenReturn(List.of(
                newApp("aB3xK9pQ2m", "Bank", List.of("Cashflow Tracking", "Assets"), "bank", RouteType.INTERNAL),
                newApp("xY7mN2kL9q", "Jira Board", List.of("Developments"), "https://example.com", RouteType.EXTERNAL)
        ));

        List<AppInfo> apps = appService.findAllApps();

        assertEquals(2, apps.size());
        assertEquals(new AppInfo("aB3xK9pQ2m", "Bank", List.of("Cashflow Tracking", "Assets"), "bank", "INTERNAL"),
                apps.get(0));
        assertEquals(new AppInfo("xY7mN2kL9q", "Jira Board", List.of("Developments"), "https://example.com", "EXTERNAL"),
                apps.get(1));
    }

    @Test
    void findAllAppsShouldAskTheRepositoryForAppsSortedByName() {
        when(appRepository.findAll(Sort.by("name"))).thenReturn(List.of());

        appService.findAllApps();

        verify(appRepository).findAll(Sort.by("name"));
    }

    @Test
    void findAllAppsShouldReturnAnEmptyListWhenThereAreNoApps() {
        when(appRepository.findAll(Sort.by("name"))).thenReturn(List.of());

        assertTrue(appService.findAllApps().isEmpty());
    }
}

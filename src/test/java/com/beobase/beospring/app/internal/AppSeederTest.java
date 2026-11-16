package com.beobase.beospring.app.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.beobase.beospring.shared.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.ActiveProfiles;
import tools.jackson.databind.ObjectMapper;

// Seeding is switched off in the test profile, so the seeder is created and run by hand here.
// Runs against the in-memory H2 database with the real apps-seed.json.
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(OutputCaptureExtension.class)
class AppSeederTest {

    // Number of apps in apps-seed.json
    private static final int SEED_COUNT = 28;

    @Autowired
    private AppRepository appRepository;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private ObjectMapper objectMapper;

    private AppSeeder seeder;

    @BeforeEach
    void setUp() {
        appRepository.deleteAll();
        seeder = new AppSeeder(appRepository, idGenerator, objectMapper);
    }

    private void runSeeder() {
        seeder.run(new DefaultApplicationArguments());
    }

    @Test
    void shouldSeedAllAppsIntoAnEmptyTable() {
        runSeeder();

        assertEquals(SEED_COUNT, appRepository.count());
    }

    @Test
    void shouldGenerateUniqueTenCharacterIds() {
        runSeeder();

        Set<String> ids = appRepository.findAll().stream()
                .map(App::getId)
                .collect(Collectors.toSet());

        assertEquals(SEED_COUNT, ids.size());
        assertTrue(ids.stream().allMatch(id -> id.length() == 10));
    }

    @Test
    void shouldMapExternalAndInternalApps() {
        runSeeder();

        App jira = appRepository.findByNameIgnoreCase("Jira Board").orElseThrow();
        assertEquals(RouteType.EXTERNAL, jira.getRouteType());
        assertEquals(List.of("Developments"), jira.getTags());
        assertTrue(jira.getRoute().startsWith("https://"));

        App userInput = appRepository.findByNameIgnoreCase("[TEMP] User Input").orElseThrow();
        assertEquals(RouteType.INTERNAL, userInput.getRouteType());
        assertEquals("user-input", userInput.getRoute());
        assertEquals(List.of("Cashflow Tracking"), userInput.getTags());
    }

    @Test
    void shouldNotDuplicateWhenRunTwice() {
        runSeeder();
        runSeeder();

        assertEquals(SEED_COUNT, appRepository.count());
    }

    @Test
    void shouldOnlyRestoreAnAppThatWasDeleted() {
        runSeeder();
        App bank = appRepository.findByNameIgnoreCase("Bank").orElseThrow();
        appRepository.delete(bank);
        assertEquals(SEED_COUNT - 1, appRepository.count());

        runSeeder();

        assertEquals(SEED_COUNT, appRepository.count());
        assertTrue(appRepository.existsByNameIgnoreCase("Bank"));
    }

    @Test
    void shouldNotDuplicateOrOverwriteAnAppThatWasEdited() {
        runSeeder();
        App bank = appRepository.findByNameIgnoreCase("Bank").orElseThrow();
        bank.setTags(List.of("Other Folder", "Assets"));
        bank.setRoute("/bank");
        appRepository.saveAndFlush(bank);

        runSeeder();

        assertEquals(SEED_COUNT, appRepository.count());
        App afterSeed = appRepository.findByNameIgnoreCase("Bank").orElseThrow();
        assertEquals(List.of("Other Folder", "Assets"), afterSeed.getTags());
        assertEquals("/bank", afterSeed.getRoute());
    }

    @Test
    void shouldLogEachNewApp(CapturedOutput output) {
        runSeeder();

        assertTrue(output.getOut().contains("Seeding new app: Bank"));
        assertTrue(output.getOut().contains("Seeded " + SEED_COUNT + " apps, skipped 0 that already exist"));
    }

    @Test
    void shouldLogEachExistingAppAsSkipped(CapturedOutput output) {
        runSeeder();
        runSeeder();

        assertTrue(output.getOut().contains("Skipping existing app: Bank"));
        assertTrue(output.getOut().contains("Seeded 0 apps, skipped " + SEED_COUNT + " that already exist"));
    }

    @Test
    void shouldMatchExistingAppsIgnoringCase() {
        runSeeder();
        App bank = appRepository.findByNameIgnoreCase("Bank").orElseThrow();
        bank.setName("BANK");
        appRepository.saveAndFlush(bank);

        runSeeder();

        assertEquals(SEED_COUNT, appRepository.count());
    }
}

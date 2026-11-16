package com.beobase.beospring.app.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.beobase.beospring.shared.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

// On startup, adds every app from apps-seed.json that is not in the database yet.
// Apps are matched by name (ignoring case); existing rows are never changed.
// Turn it off with app.seed.enabled=false
@Slf4j
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
class AppSeeder implements ApplicationRunner {

    private static final String SEED_RESOURCE = "app/apps-seed.json";

    private final AppRepository appRepository;
    private final IdGenerator idGenerator;
    private final ObjectMapper objectMapper;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        List<AppSeed> seeds = readSeeds();
        failOnDuplicateNames(seeds);

        List<App> missing = seeds.stream()
                .filter(seed -> !appRepository.existsByNameIgnoreCase(seed.name()))
                .map(this::toApp)
                .toList();

        appRepository.saveAll(missing);

        log.info("Seeded {} apps, skipped {} that already exist",
                missing.size(), seeds.size() - missing.size());
    }

    private List<AppSeed> readSeeds() {
        try (InputStream in = new ClassPathResource(SEED_RESOURCE).getInputStream()) {
            return objectMapper.readValue(in, new TypeReference<List<AppSeed>>() { });
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load " + SEED_RESOURCE, e);
        }
    }

    // The database rejects duplicate names, so fail early with a clear message
    private void failOnDuplicateNames(List<AppSeed> seeds) {
        Set<String> seen = new HashSet<>();
        List<String> duplicates = new ArrayList<>();

        for (AppSeed seed : seeds) {
            if (!seen.add(seed.name().trim().toLowerCase(Locale.ROOT))) {
                duplicates.add(seed.name());
            }
        }

        if (!duplicates.isEmpty()) {
            throw new IllegalStateException("Duplicate app names in " + SEED_RESOURCE + ": " + duplicates);
        }
    }

    private App toApp(AppSeed seed) {
        App app = new App();
        app.setId(idGenerator.generate());
        app.setName(seed.name().trim());
        app.setTags(new ArrayList<>(seed.tags()));
        app.setRoute(seed.route());
        app.setRouteType(seed.routeType());
        return app;
    }
}

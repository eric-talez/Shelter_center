package org.royshelter;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

@Component
public class Catalog {
    private static final Map<String, String> OFFICIAL_HOSTS = Map.of(
            "sfacc", "www.sfanimalcare.org", "phs", "phs-spca.org", "hssv", "www.hssv.org",
            "svaca", "www.svaca.com", "sjacs", "www.sanjoseca.gov");
    private final ArrayList<Animal> animals;
    private final ArrayList<Shelter> shelters;

    public Catalog() {
        this(load("/data/animals.json", new TypeReference<ArrayList<Animal>>() { }),
                load("/data/shelters.json", new TypeReference<ArrayList<Shelter>>() { }));
    }

    Catalog(ArrayList<Animal> animals, ArrayList<Shelter> shelters) {
        this.animals = new ArrayList<>(animals);
        this.shelters = new ArrayList<>(shelters);
        validate();
    }

    public ArrayList<Animal> animals() { return new ArrayList<>(animals); }
    public ArrayList<Shelter> shelters() { return new ArrayList<>(shelters); }

    private static <T> T load(String resource, TypeReference<T> type) {
        try (var input = Catalog.class.getResourceAsStream(resource)) {
            if (input == null) throw new IllegalStateException("Missing catalog resource: " + resource);
            return JsonMapper.builder().build().readValue(input, type);
        } catch (IOException | RuntimeException exception) {
            throw new IllegalStateException("Cannot load catalog resource: " + resource, exception);
        }
    }

    private void validate() {
        var shelterIds = new HashSet<String>();
        for (var shelter : shelters) {
            require(shelter.getId() != null && shelterIds.add(shelter.getId()), "Duplicate or missing shelter ID");
            require(officialUrl(shelter.getWebsiteUrl(), shelter.getId()), "Invalid shelter website URL");
            require(officialUrl(shelter.getSourceUrl(), shelter.getId()), "Invalid shelter source URL");
        }
        var animalIds = new HashSet<String>();
        for (var animal : animals) {
            require(animal.getId() != null && !animal.getId().isBlank() && animalIds.add(animal.getId()), "Duplicate or missing animal ID");
            require(shelterIds.contains(animal.getShelterId()), "Unknown shelter reference");
            require(animal.getSpecies() != null && Set.of("DOG", "CAT").contains(animal.getSpecies()), "Invalid species");
            require(animal.getAgeMonths() >= 0, "Negative age");
            require(animal.isDemo(), "Animal profiles must be fictional");
            require(animal.getImageUrl() != null && Set.of("", "/assets/dog.svg", "/assets/cat.svg").contains(animal.getImageUrl()), "Invalid image path");
        }
    }

    private static boolean officialUrl(String value, String shelterId) {
        if (value == null) return false;
        try {
            var uri = URI.create(value);
            return "https".equals(uri.getScheme()) && uri.getHost() != null
                    && uri.getHost().equals(OFFICIAL_HOSTS.get(shelterId)) && uri.getUserInfo() == null
                    && (uri.getPort() == -1 || uri.getPort() == 443);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private static void require(boolean valid, String message) {
        if (!valid) throw new IllegalStateException(message);
    }
}

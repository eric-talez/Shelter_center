package org.royshelter;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class CatalogTest {
    @Test void fixtureIsConsistentAndContainsTwentyDemoAnimals() {
        var catalog = new Catalog();
        var animals = catalog.animals();
        var shelters = catalog.shelters();
        assertThat(animals).hasSize(20);
        assertThat(shelters).hasSize(5);
        assertThat(animals).extracting(Animal::getId).doesNotHaveDuplicates();
        assertThat(shelters).extracting(Shelter::getId).doesNotHaveDuplicates();
        var species = new HashSet<String>();
        var purposes = new HashSet<String>();
        for (var shelter : shelters) {
            assertThat(animals.stream().filter(a -> a.getShelterId().equals(shelter.getId())).count()).isEqualTo(4);
            assertThat(shelter.getWebsiteUrl()).startsWith("https://");
            assertThat(shelter.getSourceUrl()).startsWith("https://");
        }
        for (var animal : animals) {
            assertThat(shelters).extracting(Shelter::getId).contains(animal.getShelterId());
            assertThat(animal.getAgeMonths()).isNotNegative();
            assertThat(animal.isDemo()).isTrue();
            assertThat(animal.getImageUrl()).isIn("/assets/dog.svg", "/assets/cat.svg", "");
            species.add(animal.getSpecies());
            purposes.add(animal.isAdoptable() + "/" + animal.isFosterable());
        }
        assertThat(species).containsExactlyInAnyOrder("DOG", "CAT");
        assertThat(purposes).containsExactlyInAnyOrder("true/false", "false/true", "true/true");
        var luna = animals.getFirst();
        assertThat(luna.getId()).isEqualTo("mock-sfacc-01");
        assertThat(luna.getName()).isEqualTo("Luna");
        assertThat(luna.getSpecies()).isEqualTo("DOG");
        assertThat(luna.getBreed()).isEqualTo("Terrier mix");
        assertThat(luna.getAgeMonths()).isEqualTo(24);
        assertThat(luna.getShelterId()).isEqualTo("sfacc");
        assertThat(luna.isAdoptable()).isTrue();
        assertThat(luna.isFosterable()).isFalse();
        assertThat(luna.getImageUrl()).isEqualTo("/assets/dog.svg");
    }

    @Test void returnedListsCannotChangeStoredCatalog() {
        var catalog = new Catalog();
        catalog.animals().clear();
        catalog.shelters().clear();
        assertThat(catalog.animals()).hasSize(20);
        assertThat(catalog.shelters()).hasSize(5);
    }
    @Test void rejectsInvalidFixturesAtStartup() {
        var shelter = new Shelter("sfacc", "SF Animal Care & Control", "San Francisco",
                "https://www.sfanimalcare.org/adoptable-animals/", "+1-415-554-6364", "https://www.sfanimalcare.org/contact/");
        var valid = demo("a", "DOG", 24, "/assets/dog.svg", "sfacc", true);
        for (var bad : List.of(demo("a", "BIRD", 24, "", "sfacc", true),
                demo("a", "CAT", -1, "", "sfacc", true), demo("a", "CAT", 24, "https://example.com/cat.png", "sfacc", true),
                demo("a", "CAT", 24, "", "unknown", true), demo("a", null, 24, "", "sfacc", true),
                demo("a", "CAT", 24, null, "sfacc", true), demo("a", "CAT", 24, "", "sfacc", false))) {
            assertThatThrownBy(() -> new Catalog(new ArrayList<>(List.of(bad)), new ArrayList<>(List.of(shelter))))
                    .isInstanceOf(IllegalStateException.class);
        }
        assertThatThrownBy(() -> new Catalog(new ArrayList<>(List.of(valid, valid)), new ArrayList<>(List.of(shelter))))
                .isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> new Catalog(new ArrayList<>(List.of(valid)), new ArrayList<>(List.of(shelter, shelter))))
                .isInstanceOf(IllegalStateException.class);
        for (String url : List.of("http://www.sfanimalcare.org/adoptable-animals/", "https://example.com/", "https://www.sfanimalcare.org.evil.example/")) {
            var bad = new Shelter("sfacc", "SF Animal Care & Control", "San Francisco", url, "+1-415-554-6364", "https://www.sfanimalcare.org/contact/");
            assertThatThrownBy(() -> new Catalog(new ArrayList<>(List.of(valid)), new ArrayList<>(List.of(bad))))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    private Animal demo(String id, String species, int age, String image, String shelterId, boolean demo) {
        return new Animal(id, "Demo", species, "Mix", age, "Fictional profile", image, shelterId, true, false, demo);
    }
}

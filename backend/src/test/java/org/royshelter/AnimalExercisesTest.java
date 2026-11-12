package org.royshelter;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

@Tag("exercise")
class AnimalExercisesTest {
    private final AnimalExercises exercises = new AnimalExercises();
    private final Animal puppy = new Animal("a", "Luna", "DOG", "Terrier mix", 0, "가상 강아지", "", "sfacc", true, false, true);
    private final Animal cat = new Animal("b", "Milo", "CAT", "Domestic shorthair", 24, "가상 고양이", "", "phs", false, true, true);
    private final Animal dog = new Animal("c", "Poppy", "DOG", "Retriever mix", 25, "가상 개", "", "sfacc", true, true, true);
    private ArrayList<Animal> animals() { return new ArrayList<>(List.of(puppy, cat, dog)); }

    @Nested class Lesson1 {
        @Test void matchesSpeciesWithoutChangingSource() {
            var source = animals();
            // A distinct String catches reference equality instead of content equality.
            assertThat(exercises.filterBySpecies(source, new String("DOG")))
                    .isNotSameAs(source).containsExactly(puppy, dog);
            assertThat(source).containsExactly(puppy, cat, dog);
        }
        @Test void noMatchingSpeciesGivesEmptyList() {
            assertThat(exercises.filterBySpecies(new ArrayList<>(List.of(puppy)), new String("CAT"))).isEmpty();
        }
    }
    @Nested class Lesson2 {
        @Test void ageBoundaryIsInclusive() {
            var source = animals();
            assertThat(exercises.filterByMaxAge(source, 24)).isNotSameAs(source).containsExactly(puppy, cat);
            assertThat(source).containsExactly(puppy, cat, dog);
        }
        @Test void zeroMonthsIsValid() {
            var source = animals();
            assertThat(exercises.filterByMaxAge(source, 0)).isNotSameAs(source).containsExactly(puppy);
            assertThat(source).containsExactly(puppy, cat, dog);
        }
    }
    @Nested class Lesson3 {
        @Test void findsExistingId() { assertThat(exercises.findById(animals(), new String("b"))).isSameAs(cat); }
        @Test void missingIdGivesNull() { assertThat(exercises.findById(animals(), "missing")).isNull(); }
    }
    @Nested class Lesson4 {
        @Test void trimmedQueryMatchesNameIgnoringCase() {
            var source = animals();
            assertThat(exercises.filterByQuery(source, "  LuN  ")).isNotSameAs(source).containsExactly(puppy);
            assertThat(source).containsExactly(puppy, cat, dog);
        }
        @Test void queryAlsoMatchesBreedSubstring() {
            var source = animals();
            assertThat(exercises.filterByQuery(source, "MIX")).isNotSameAs(source).containsExactly(puppy, dog);
            assertThat(source).containsExactly(puppy, cat, dog);
        }
        @Test void emptyQueryGivesAllAnimals() {
            var source = animals();
            assertThat(exercises.filterByQuery(source, "  ")).isNotSameAs(source).containsExactly(puppy, cat, dog);
            assertThat(source).containsExactly(puppy, cat, dog);
        }
        @Test void adoptionUsesAdoptableFlagIndependently() {
            var source = animals();
            assertThat(exercises.filterByPurpose(source, new String("ADOPT")))
                    .isNotSameAs(source).containsExactly(puppy, dog);
            assertThat(source).containsExactly(puppy, cat, dog);
        }
        @Test void fosteringUsesFosterableFlagIndependently() {
            var source = animals();
            assertThat(exercises.filterByPurpose(source, new String("FOSTER")))
                    .isNotSameAs(source).containsExactly(cat, dog);
            assertThat(source).containsExactly(puppy, cat, dog);
        }
    }
    @Nested class Lesson6 {
        @Test void countsDistinctSheltersWithoutChangingSource() {
            var source = animals();
            assertThat(exercises.countDistinctShelters(source)).isEqualTo(2);
            assertThat(source).containsExactly(puppy, cat, dog);
        }
        @Test void emptyListHasZeroShelters() {
            assertThat(exercises.countDistinctShelters(new ArrayList<>())).isZero();
        }
    }
    @Nested class Lesson7 {
        @Test void countsBothSpeciesWithoutChangingSource() {
            var source = animals();
            assertThat(exercises.countBySpecies(source)).containsOnly(entry("DOG", 2), entry("CAT", 1));
            assertThat(source).containsExactly(puppy, cat, dog);
        }
        @Test void emptyListIncludesBothZeroCounts() {
            assertThat(exercises.countBySpecies(new ArrayList<>())).containsOnly(entry("DOG", 0), entry("CAT", 0));
        }
        @Test void absentSpeciesStillHasAZeroCount() {
            var source = new ArrayList<>(List.of(puppy, dog));
            assertThat(exercises.countBySpecies(source)).containsOnly(entry("DOG", 2), entry("CAT", 0));
            assertThat(source).containsExactly(puppy, dog);
        }
    }
}

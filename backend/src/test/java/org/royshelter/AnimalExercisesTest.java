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
            assertThat(exercises.filterBySpecies(source, "DOG")).containsExactly(puppy, dog);
            assertThat(source).containsExactly(puppy, cat, dog);
        }
        @Test void noMatchingSpeciesGivesEmptyList() {
            assertThat(exercises.filterBySpecies(new ArrayList<>(List.of(puppy)), "CAT")).isEmpty();
        }
    }
    @Nested class Lesson2 {
        @Test void ageBoundaryIsInclusive() {
            assertThat(exercises.filterByMaxAge(animals(), 24)).containsExactly(puppy, cat);
        }
        @Test void zeroMonthsIsValid() {
            assertThat(exercises.filterByMaxAge(animals(), 0)).containsExactly(puppy);
        }
    }
    @Nested class Lesson3 {
        @Test void findsExistingId() { assertThat(exercises.findById(animals(), "b")).isSameAs(cat); }
        @Test void missingIdGivesNull() { assertThat(exercises.findById(animals(), "missing")).isNull(); }
    }
    @Nested class Lesson4 {
        @Test void trimmedQueryMatchesNameIgnoringCase() {
            assertThat(exercises.filterByQuery(animals(), "  LuN  ")).containsExactly(puppy);
        }
        @Test void queryAlsoMatchesBreedSubstring() {
            assertThat(exercises.filterByQuery(animals(), "MIX")).containsExactly(puppy, dog);
        }
        @Test void emptyQueryGivesAllAnimals() {
            assertThat(exercises.filterByQuery(animals(), "  ")).containsExactly(puppy, cat, dog);
        }
        @Test void adoptionUsesAdoptableFlagIndependently() {
            assertThat(exercises.filterByPurpose(animals(), "ADOPT")).containsExactly(puppy, dog);
        }
        @Test void fosteringUsesFosterableFlagIndependently() {
            assertThat(exercises.filterByPurpose(animals(), "FOSTER")).containsExactly(cat, dog);
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

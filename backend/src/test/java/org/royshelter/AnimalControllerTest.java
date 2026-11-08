package org.royshelter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AnimalControllerTest {
    private final Catalog catalog = new Catalog();
    private MockMvc api(AnimalExercises exercises) {
        return MockMvcBuilders.standaloneSetup(new AnimalController(catalog, exercises))
                .setControllerAdvice(new ApiErrors()).build();
    }
    private AnimalExercises unfinished() {
        return new AnimalExercises() {
            @Override public ArrayList<Animal> filterBySpecies(ArrayList<Animal> a, String s) {
                throw new ExerciseNotImplementedException("lesson-1");
            }
        };
    }
    private static class SummaryExercises extends AnimalExercises {
        private final Integer shelters;
        private final HashMap<String, Integer> species;
        private final ArrayList<Animal> filtered;
        private final ArrayList<String> calls = new ArrayList<>();

        SummaryExercises(Integer shelters, HashMap<String, Integer> species, ArrayList<Animal> filtered) {
            this.shelters = shelters;
            this.species = species;
            this.filtered = filtered;
        }
        @Override public ArrayList<Animal> filterBySpecies(ArrayList<Animal> animals, String species) {
            return filtered;
        }
        @Override public int countDistinctShelters(ArrayList<Animal> animals) {
            if (filtered != null) assertThat(animals).isSameAs(filtered);
            calls.add("lesson-6");
            if (shelters == null) throw new ExerciseNotImplementedException("lesson-6");
            return shelters;
        }
        @Override public HashMap<String, Integer> countBySpecies(ArrayList<Animal> animals) {
            if (filtered != null) assertThat(animals).isSameAs(filtered);
            calls.add("lesson-7");
            if (species == null) throw new ExerciseNotImplementedException("lesson-7");
            return species;
        }
    }
    @Test void unfinishedSummariesKeepCatalogAvailable() throws Exception {
        var exercises = new SummaryExercises(null, null, null);
        api(exercises).perform(get("/api/animals"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.total").value(20))
                .andExpect(jsonPath("$.items.length()").value(20))
                .andExpect(jsonPath("$.summary.shelterCount").value(org.hamcrest.Matchers.nullValue()))
                .andExpect(jsonPath("$.summary.speciesCounts").value(org.hamcrest.Matchers.nullValue()))
                .andExpect(jsonPath("$.summary.unfinishedLessons").value(org.hamcrest.Matchers.contains("lesson-6", "lesson-7")));
        assertThat(exercises.calls).containsExactly("lesson-6", "lesson-7");
    }
    @Test void summariesCompleteIndependentlyOnTheSameFilteredList() throws Exception {
        var filtered = new ArrayList<>(List.of(catalog.animals().getFirst()));
        for (boolean shelterComplete : List.of(true, false)) {
            var exercises = new SummaryExercises(shelterComplete ? 1 : null,
                    shelterComplete ? null : new HashMap<>(Map.of("DOG", 1, "CAT", 0)), filtered);
            var response = api(exercises).perform(get("/api/animals").param("species", "DOG"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.total").value(1))
                    .andExpect(jsonPath("$.items[0].id").value("mock-sfacc-01"))
                    .andExpect(jsonPath("$.summary.unfinishedLessons")
                            .value(org.hamcrest.Matchers.contains(shelterComplete ? "lesson-7" : "lesson-6")));
            if (shelterComplete) {
                response.andExpect(jsonPath("$.summary.shelterCount").value(1))
                        .andExpect(jsonPath("$.summary.speciesCounts").value(org.hamcrest.Matchers.nullValue()));
            } else {
                response.andExpect(jsonPath("$.summary.shelterCount").value(org.hamcrest.Matchers.nullValue()))
                        .andExpect(jsonPath("$.summary.speciesCounts.DOG").value(1))
                        .andExpect(jsonPath("$.summary.speciesCounts.CAT").value(0));
            }
            assertThat(exercises.calls).containsExactly("lesson-6", "lesson-7");
        }
    }
    @Test void completedEmptySummaryHasRealZeroCounts() throws Exception {
        var exercises = new SummaryExercises(0, new HashMap<>(Map.of("DOG", 0, "CAT", 0)), new ArrayList<>());
        api(exercises).perform(get("/api/animals").param("species", "CAT"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.summary.shelterCount").value(0))
                .andExpect(jsonPath("$.summary.speciesCounts.DOG").value(0))
                .andExpect(jsonPath("$.summary.speciesCounts.CAT").value(0))
                .andExpect(jsonPath("$.summary.unfinishedLessons").value(org.hamcrest.Matchers.empty()));
        assertThat(exercises.calls).containsExactly("lesson-6", "lesson-7");
    }
    @Test @ExtendWith(OutputCaptureExtension.class)
    void unexpectedSummaryFailureRetainsSanitizedErrorAndDiagnostic(CapturedOutput output) throws Exception {
        for (boolean breakShelters : List.of(true, false)) {
            var broken = new SummaryExercises(1, new HashMap<>(Map.of("DOG", 1, "CAT", 0)), null) {
                @Override public int countDistinctShelters(ArrayList<Animal> animals) {
                    if (breakShelters) throw new IllegalStateException("private summary diagnostic");
                    return super.countDistinctShelters(animals);
                }
                @Override public HashMap<String, Integer> countBySpecies(ArrayList<Animal> animals) {
                    throw new IllegalStateException("private summary diagnostic");
                }
            };
            api(broken).perform(get("/api/animals"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                    .andExpect(jsonPath("$.lessonId").isEmpty())
                    .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("private summary diagnostic"))))
                    .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("IllegalStateException"))))
                    .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("org.royshelter"))));
        }
        assertThat(output.getAll()).contains("ERROR", "java.lang.IllegalStateException: private summary diagnostic",
                "\tat org.royshelter.AnimalControllerTest");
    }
    @Test void listsCatalogWithoutExercises() throws Exception {
        api(new AnimalExercises()).perform(get("/api/animals"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.total").value(20))
                .andExpect(jsonPath("$.items[0].id").value("mock-sfacc-01"))
                .andExpect(jsonPath("$.items[0].shelterId").value("sfacc"))
                .andExpect(jsonPath("$.items[0].shelter").doesNotExist());
    }
    @Test void listsShelters() throws Exception {
        api(new AnimalExercises()).perform(get("/api/shelters"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items.length()").value(5))
                .andExpect(jsonPath("$.items[0].phone").value("+1-415-554-6364"));
    }
    @Test void validatesBeforeExercise() throws Exception {
        api(unfinished()).perform(get("/api/animals").param("species", "DOG").param("maxAgeMonths", "-1"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.lessonId").isEmpty());
    }
    @Test void rejectsMalformedQueries() throws Exception {
        for (String[] query : new String[][]{{"maxAgeMonths","abc"}, {"unknown","x"},
                {"species","BIRD"}, {"purpose","OTHER"}, {"maxAgeMonths","2147483648"}, {"maxAgeMonths","1.5"}}) {
            api(unfinished()).perform(get("/api/animals").param(query[0], query[1]))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
        }
        for (String key : List.of("species", "maxAgeMonths", "q", "purpose")) {
            api(unfinished()).perform(get("/api/animals").param(key, "", ""))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
        }
    }
    @Test void shelterFilteringIsUnsupportedEvenForKnownOrEmptyIds() throws Exception {
        for (String id : List.of("sfacc", "missing", "")) {
            api(unfinished()).perform(get("/api/animals").param("shelterId", id))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
        }
    }
    @Test void keywordAndPurposeReportLesson4() throws Exception {
        var unfinishedSearch = new AnimalExercises() {
            @Override public ArrayList<Animal> filterByQuery(ArrayList<Animal> a, String q) {
                throw new ExerciseNotImplementedException("lesson-4");
            }
            @Override public ArrayList<Animal> filterByPurpose(ArrayList<Animal> a, String p) {
                throw new ExerciseNotImplementedException("lesson-4");
            }
        };
        for (String[] query : new String[][]{{"q", "Luna"}, {"purpose", "FOSTER"}}) {
            api(unfinishedSearch).perform(get("/api/animals").param(query[0], query[1]))
                    .andExpect(status().is(501))
                    .andExpect(jsonPath("$.code").value("EXERCISE_NOT_IMPLEMENTED"))
                    .andExpect(jsonPath("$.lessonId").value("lesson-4"));
        }
    }
    @Test void reportsUnfinishedLesson() throws Exception {
        api(unfinished()).perform(get("/api/animals").param("species", "CAT"))
                .andExpect(status().is(501)).andExpect(jsonPath("$.code").value("EXERCISE_NOT_IMPLEMENTED"))
                .andExpect(jsonPath("$.lessonId").value("lesson-1"));
    }
    @Test void emptyMatchesAreSuccess() throws Exception {
        var completed = new AnimalExercises() {
            @Override public ArrayList<Animal> filterBySpecies(ArrayList<Animal> animals, String species) { return new ArrayList<>(); }
        };
        api(completed).perform(get("/api/animals").param("species", "CAT"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.items").isEmpty())
                .andExpect(jsonPath("$.total").value(0));
    }
    @Test void absentAnimalIsNotFound() throws Exception {
        var completed = new AnimalExercises() {
            @Override public Animal findById(ArrayList<Animal> animals, String id) { return null; }
        };
        api(completed).perform(get("/api/animals/missing"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }
    @Test void completedDetailUsesLookup() throws Exception {
        var completed = new AnimalExercises() {
            @Override public Animal findById(ArrayList<Animal> animals, String id) {
                assertThat(id).isEqualTo("mock-sfacc-01");
                return animals.getFirst();
            }
        };
        api(completed).perform(get("/api/animals/mock-sfacc-01"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value("mock-sfacc-01"));
    }
    @Test @ExtendWith(OutputCaptureExtension.class)
    void unexpectedFailureIsNotLesson(CapturedOutput output) throws Exception {
        var broken = new AnimalExercises() {
            @Override public ArrayList<Animal> filterBySpecies(ArrayList<Animal> a, String s) {
                throw new IllegalStateException("private diagnostic");
            }
        };
        api(broken).perform(get("/api/animals").param("species", "CAT"))
                .andExpect(status().isInternalServerError()).andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.lessonId").isEmpty())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("private diagnostic"))))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("IllegalStateException"))))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("org.royshelter"))));
        assertThat(output.getAll()).contains("ERROR", "java.lang.IllegalStateException: private diagnostic",
                "\tat org.royshelter.AnimalControllerTest");
    }
    @Test void whitespaceQueryIsUnfiltered() throws Exception {
        api(new AnimalExercises()).perform(get("/api/animals").param("q", "  "))
                .andExpect(status().isOk()).andExpect(jsonPath("$.total").value(20));
    }
    @Test void emptySelectionIsUnfiltered() throws Exception {
        api(new AnimalExercises()).perform(get("/api/animals").param("species", "").param("maxAgeMonths", "")
                .param("purpose", ""))
                .andExpect(status().isOk()).andExpect(jsonPath("$.total").value(20));
    }
    @Test void composesValidatedFiltersInOrderAndPassesPreviousResults() throws Exception {
        var calls = new ArrayList<String>();
        var completed = new AnimalExercises() {
            private ArrayList<Animal> step(ArrayList<Animal> animals, String label) {
                calls.add(label);
                assertThat(animals).hasSize(21 - calls.size());
                return new ArrayList<>(animals.subList(0, animals.size() - 1));
            }
            @Override public ArrayList<Animal> filterBySpecies(ArrayList<Animal> a, String s) { assertThat(s).isEqualTo("DOG"); return step(a,"species"); }
            @Override public ArrayList<Animal> filterByMaxAge(ArrayList<Animal> a, int age) { assertThat(age).isEqualTo(24); return step(a,"age"); }
            @Override public ArrayList<Animal> filterByQuery(ArrayList<Animal> a, String q) { assertThat(q).isEqualTo("Luna"); return step(a,"query"); }
            @Override public ArrayList<Animal> filterByPurpose(ArrayList<Animal> a, String p) { assertThat(p).isEqualTo("ADOPT"); return step(a,"purpose"); }
        };
        api(completed).perform(get("/api/animals").param("species","DOG").param("maxAgeMonths","24")
                .param("q"," Luna ").param("purpose","ADOPT"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.total").value(16));
        assertThat(calls).containsExactly("species","age","query","purpose");
        assertThat(catalog.animals()).hasSize(20);
    }
}

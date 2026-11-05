package org.royshelter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.Test;
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
        for (String[] query : new String[][]{{"maxAgeMonths","abc"}, {"unknown","x"}, {"shelterId","missing"},
                {"species","BIRD"}, {"purpose","OTHER"}, {"maxAgeMonths","2147483648"}, {"maxAgeMonths","1.5"}}) {
            api(unfinished()).perform(get("/api/animals").param(query[0], query[1]))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
        }
        for (String key : List.of("species", "maxAgeMonths", "q", "purpose")) {
            api(unfinished()).perform(get("/api/animals").param(key, "", ""))
                    .andExpect(status().isBadRequest()).andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
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
    @Test void unexpectedFailureIsNotLesson() throws Exception {
        var broken = new AnimalExercises() {
            @Override public ArrayList<Animal> filterBySpecies(ArrayList<Animal> a, String s) {
                throw new IllegalStateException("private diagnostic");
            }
        };
        api(broken).perform(get("/api/animals").param("species", "CAT"))
                .andExpect(status().isInternalServerError()).andExpect(jsonPath("$.code").value("INTERNAL_ERROR"))
                .andExpect(jsonPath("$.lessonId").isEmpty()).andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("private diagnostic"))));
    }
    @Test void whitespaceQueryIsUnfiltered() throws Exception {
        api(new AnimalExercises()).perform(get("/api/animals").param("q", "  "))
                .andExpect(status().isOk()).andExpect(jsonPath("$.total").value(20));
    }
    @Test void emptySelectionIsUnfiltered() throws Exception {
        api(new AnimalExercises()).perform(get("/api/animals").param("species", "").param("maxAgeMonths", "")
                .param("purpose", "").param("shelterId", "", ""))
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
            @Override public ArrayList<Animal> filterByShelters(ArrayList<Animal> a, HashSet<String> ids) { assertThat(ids).containsExactlyInAnyOrder("sfacc","phs"); return step(a,"shelter"); }
            @Override public ArrayList<Animal> filterByQuery(ArrayList<Animal> a, String q) { assertThat(q).isEqualTo("Luna"); return step(a,"query"); }
            @Override public ArrayList<Animal> filterByPurpose(ArrayList<Animal> a, String p) { assertThat(p).isEqualTo("ADOPT"); return step(a,"purpose"); }
        };
        api(completed).perform(get("/api/animals").param("species","DOG").param("maxAgeMonths","24")
                .param("shelterId","sfacc","phs","sfacc").param("q"," Luna ").param("purpose","ADOPT"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.total").value(15));
        assertThat(calls).containsExactly("species","age","shelter","query","purpose");
        assertThat(catalog.animals()).hasSize(20);
    }
}

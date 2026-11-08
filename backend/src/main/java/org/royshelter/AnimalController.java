package org.royshelter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnimalController {
    private final Catalog catalog;
    private final AnimalExercises exercises;

    public AnimalController(Catalog catalog, AnimalExercises exercises) {
        this.catalog = catalog;
        this.exercises = exercises;
    }

    @GetMapping("/api/animals")
    public Map<String, Object> animals(@RequestParam MultiValueMap<String, String> parameters) {
        var search = validate(parameters);
        var items = catalog.animals();
        if (!search.species().isEmpty()) items = exercises.filterBySpecies(items, search.species());
        if (search.maxAgeMonths() != null) items = exercises.filterByMaxAge(items, search.maxAgeMonths());
        if (!search.query().isEmpty()) items = exercises.filterByQuery(items, search.query());
        if (!search.purpose().isEmpty()) items = exercises.filterByPurpose(items, search.purpose());
        Integer shelterCount = null;
        HashMap<String, Integer> speciesCounts = null;
        var unfinishedLessons = new ArrayList<String>();
        try { shelterCount = exercises.countDistinctShelters(items); }
        catch (ExerciseNotImplementedException exception) { unfinishedLessons.add("lesson-6"); }
        try { speciesCounts = exercises.countBySpecies(items); }
        catch (ExerciseNotImplementedException exception) { unfinishedLessons.add("lesson-7"); }
        return Map.of("items", items, "total", items.size(),
                "summary", new Summary(shelterCount, speciesCounts, unfinishedLessons));
    }

    @GetMapping("/api/shelters")
    public Map<String, Object> shelters() { return Map.of("items", catalog.shelters()); }

    @GetMapping("/api/animals/{id}")
    public Animal animal(@PathVariable String id) {
        var animal = exercises.findById(catalog.animals(), id);
        if (animal == null) throw new ApiErrors.NotFound();
        return animal;
    }

    private Search validate(MultiValueMap<String, String> parameters) {
        var allowed = Set.of("species", "maxAgeMonths", "q", "purpose");
        for (var key : parameters.keySet()) {
            if (!allowed.contains(key)) throw invalid("알 수 없는 검색 조건입니다.");
            if (parameters.get(key).size() != 1) {
                throw invalid("검색 조건은 한 번만 지정하세요.");
            }
        }
        var species = value(parameters, "species");
        if (!species.isEmpty() && !Set.of("DOG", "CAT").contains(species)) throw invalid("동물 종류를 확인하세요.");
        Integer maxAge = null;
        var age = value(parameters, "maxAgeMonths");
        if (!age.isEmpty()) {
            if (!age.matches("[0-9]+")) throw invalid("나이는 0 이상의 정수여야 합니다.");
            try { maxAge = Integer.valueOf(age); }
            catch (NumberFormatException exception) { throw invalid("나이가 너무 큽니다."); }
        }
        var purpose = value(parameters, "purpose");
        if (!purpose.isEmpty() && !Set.of("ADOPT", "FOSTER").contains(purpose)) throw invalid("연결 목적을 확인하세요.");
        return new Search(species, maxAge, value(parameters, "q").trim(), purpose);
    }

    private static String value(MultiValueMap<String, String> parameters, String key) {
        var value = parameters.getFirst(key);
        return value == null ? "" : value;
    }

    private static ApiErrors.InvalidRequest invalid(String message) { return new ApiErrors.InvalidRequest(message); }

    public record Summary(Integer shelterCount, HashMap<String, Integer> speciesCounts, List<String> unfinishedLessons) { }
    private record Search(String species, Integer maxAgeMonths, String query, String purpose) { }
}

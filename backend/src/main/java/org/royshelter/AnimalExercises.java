package org.royshelter;

import java.util.ArrayList;
import org.springframework.stereotype.Component;

@Component
public class AnimalExercises {
    public ArrayList<Animal> filterBySpecies(ArrayList<Animal> animals, String species) {
        // TODO (lesson-1): species와 일치하는 동물을 새 ArrayList에 담아 반환하세요.
        throw new ExerciseNotImplementedException("lesson-1");
    }

    public ArrayList<Animal> filterByMaxAge(ArrayList<Animal> animals, int maxAgeMonths) {
        // TODO (lesson-2): 나이가 maxAgeMonths 이하인 동물을 골라 반환하세요.
        throw new ExerciseNotImplementedException("lesson-2");
    }

    public Animal findById(ArrayList<Animal> animals, String id) {
        // TODO (lesson-3): HashMap에 id와 동물을 저장하고 원하는 id를 조회하세요.
        throw new ExerciseNotImplementedException("lesson-3");
    }

    public ArrayList<Animal> filterByQuery(ArrayList<Animal> animals, String query) {
        // TODO (lesson-4): 이름 또는 품종에 검색어가 포함된 동물을 골라 반환하세요.
        throw new ExerciseNotImplementedException("lesson-4");
    }

    public ArrayList<Animal> filterByPurpose(ArrayList<Animal> animals, String purpose) {
        // TODO (lesson-4): 입양 또는 임시 보호가 가능한 동물을 골라 반환하세요.
        throw new ExerciseNotImplementedException("lesson-4");
    }

}

package org.royshelter;

import java.util.ArrayList;
import java.util.HashMap;
import org.springframework.stereotype.Component;

@Component
public class AnimalExercises {
    /**
     * Select animals of the requested species. Input: a non-null list and DOG or CAT.
     * Output: a new list in source order; for [DOG, CAT, DOG] and DOG, return [DOG, DOG].
     * Empty input or no matches gives an empty list. Never change the input list.
     */
    public ArrayList<Animal> filterBySpecies(ArrayList<Animal> animals, String species) {
        // Step 1: Prepare a new local ArrayList<Animal> for matching animals.
        // Step 2: Inspect each animal's getSpecies() value in a loop.
        // Step 3: Decide whether its species equals the requested String, then collect matches.
        // Step 4: Return the result list; remove the final lesson exception when complete.
        throw new ExerciseNotImplementedException("lesson-1");
    }

    /**
     * Select animals up to an inclusive age limit. Input: a non-null list and nonnegative months.
     * Output: a new list in source order; ages [0, 24, 25] with limit 24 gives [0, 24].
     * Include newborns at limit 0; empty input or no matches gives an empty list.
     * Never change the input list.
     */
    public ArrayList<Animal> filterByMaxAge(ArrayList<Animal> animals, int maxAgeMonths) {
        // Step 1: Prepare a new local ArrayList<Animal> for matching animals.
        // Step 2: Inspect each animal's getAgeMonths() value in a loop.
        // Step 3: Compare the age with the inclusive maximum, then collect matches.
        // Step 4: Return the result list; remove the final lesson exception when complete.
        throw new ExerciseNotImplementedException("lesson-2");
    }

    /**
     * Find an animal by ID using a map. Input: a non-null list with unique IDs and a requested ID.
     * Output: the matching Animal, or null; IDs [a, b] and request b returns animal b.
     * Empty input or a missing ID gives null. Never change the input list or its order.
     */
    public Animal findById(ArrayList<Animal> animals, String id) {
        // Step 1: Prepare a new local HashMap<String, Animal> to index animals.
        // Step 2: Inspect each animal's getId() value in a loop.
        // Step 3: Associate each ID with its animal, then look up the requested ID.
        // Step 4: Return the lookup result; remove the final lesson exception when complete.
        throw new ExerciseNotImplementedException("lesson-3");
    }

    /**
     * Search names or breeds by substring, ignoring case and surrounding search whitespace.
     * Input: a non-null list and non-null query. Output: a new list in source order.
     * Example: query "  MIX  " matches breed "Terrier mix".
     * A normalized empty query matches all animals; empty input gives an empty list.
     * Never change the input list. ExerciseHelpers.normalizeText handles text preparation.
     */
    public ArrayList<Animal> filterByQuery(ArrayList<Animal> animals, String query) {
        // Step 1: Prepare a new local ArrayList<Animal> and normalize the query with ExerciseHelpers.
        // Step 2: Inspect and normalize each animal's getName() and getBreed() in a loop.
        // Step 3: Decide whether either text contains the query, then collect each match once.
        // Step 4: Return the result list; remove the final lesson exception when complete.
        throw new ExerciseNotImplementedException("lesson-4");
    }

    /**
     * Select animals with the requested opportunity flag. Input: a non-null list and ADOPT or FOSTER.
     * Output: a new list in source order; an adoptable-only animal matches ADOPT but not FOSTER.
     * Flags are independent: an animal may match both purposes or neither.
     * Empty input or no matches gives an empty list. Never change the input list.
     */
    public ArrayList<Animal> filterByPurpose(ArrayList<Animal> animals, String purpose) {
        // Step 1: Prepare a new local ArrayList<Animal> for matching animals.
        // Step 2: Inspect each animal's isAdoptable() and isFosterable() flags in a loop.
        // Step 3: Choose the flag for the requested purpose, then collect animals with that flag.
        // Step 4: Return the result list; remove the final lesson exception when complete.
        throw new ExerciseNotImplementedException("lesson-4");
    }

    /**
     * Count distinct shelters represented in the current results. Input: a non-null animal list.
     * Output: the number of unique shelter IDs; [sfacc, phs, sfacc] gives 2.
     * Repeated IDs count once; empty input gives 0. Never change the input list or its order.
     */
    public int countDistinctShelters(ArrayList<Animal> animals) {
        // Step 1: Prepare a new local HashSet<String> for shelter IDs (add its import).
        // Step 2: Inspect each animal's getShelterId() value in a loop.
        // Step 3: Collect shelter IDs in the set so repeated IDs are represented only once.
        // Step 4: Return the set's size; remove the final lesson exception when complete.
        throw new ExerciseNotImplementedException("lesson-6");
    }

    /**
     * Count current results by species. Input: a non-null list containing DOG and CAT animals.
     * Output: a new HashMap with both keys; [DOG, CAT, DOG] gives DOG=2, CAT=1.
     * Empty input gives DOG=0, CAT=0; a missing species still has its zero key.
     * Never change the input list or its order. ExerciseHelpers.supportedSpecies provides a fresh array.
     */
    public HashMap<String, Integer> countBySpecies(ArrayList<Animal> animals) {
        // Step 1: Prepare a new local HashMap<String, Integer>; use the supported species array for zero keys.
        // Step 2: Inspect each animal's getSpecies() value in a loop.
        // Step 3: Read the current count for that species and store its increased count.
        // Step 4: Return the counts map; remove the final lesson exception when complete.
        throw new ExerciseNotImplementedException("lesson-7");
    }
}

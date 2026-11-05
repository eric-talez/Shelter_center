# Search summary and guided collection exercises

Status: Proposed for user approval.

## Intent

Extend the existing Java/Spring Boot and Python/NiceGUI learning starter so a beginner can complete useful website behavior with loops, conditions, arrays, ArrayList, HashSet and HashMap. Student work stays unfinished, but each stub explains its milestones in English. Framework plumbing and unfamiliar peripheral operations are provided as documented helpers.

The user selected search-result summaries over a favorites feature. The catalog continues to aggregate all shelters and link animal names and calls to action directly to official shelter websites. All animal profiles remain fictional.

## Approaches considered

1. **Search-result summaries — selected.** Count distinct shelters and animals by species from the current results. This naturally exercises sets and maps using a single supplied list, without persistent or per-user state.
2. **Favorites — deferred.** Store selected animal IDs and resolve them back to profiles. This also uses sets/maps but introduces selection state and its lifetime, adding complexity before the learner knows HTTP or Spring.

## Scope and boundaries

- Preserve the existing stack, fixtures, official links, mock-data notices and local run workflow.
- Keep the existing five Java exercise methods unfinished and improve their instructions.
- Add two Java exercise stubs for distinct shelter counts and species counts.
- Connect summary results to the existing catalog page and make the Python badge exercise usable on catalog cards without completing ID lookup first.
- Supply small concrete helpers; do not add repositories, service interfaces, persistence, authentication, new dependencies or a generalized lesson engine.
- Do not add a shelter filter, real animal ingestion, favorites, deployment, completed student solutions or automatic solution fallback.

## Student work and provided work

Existing lesson IDs remain stable, avoiding changes to existing test selectors and links. The learning guide can order the lessons differently from their IDs.

| Lesson | Student method/work | Core practice |
| --- | --- | --- |
| 1 | Filter by species | Loop, condition, new ArrayList, string equality |
| 2 | Filter by inclusive maximum age | ArrayList and numeric boundaries |
| 3, optional | Find one animal by ID | Build and query HashMap<String, Animal> |
| 4 | Filter by normalized name/breed query and purpose | ArrayList, OR conditions, independent booleans |
| 5 | Produce Python opportunity labels for one animal | Dictionary reads, independent conditions, appending to a list |
| 6, new | countDistinctShelters(ArrayList<Animal>) returns int | Add shelter IDs to HashSet and return its size |
| 7, new | countBySpecies(ArrayList<Animal>) returns HashMap<String, Integer> | Initialize counts, read/update values by key |

Every Java method has English instructions covering purpose, input, output, a small example, numbered steps, edge cases, and the completion marker to remove. Steps identify the collection to prepare, what to inspect during the loop, and the result to return. Do not supply the complete loop, collection mutations or answer predicate. Stubs compile and deliberately report their lesson until implemented; they never return fabricated empty/zero results to look complete.

Students must preserve the input list and order where applicable. Collections are local to each call. No streams, lambdas, advanced generic abstractions or Spring knowledge are needed in student implementations.

Provided Java helpers are limited to peripheral work:

- Normalize search text with trimming and locale-independent lowercasing. Students still decide how to match an animal's name/breed and build the result list.
- Supply a fresh String[] of supported species (DOG, CAT). Students can traverse this array to initialize their HashMap, then traverse animals to accumulate counts. Helpers do not build or update the map for them.

Helpers have English documentation explaining arguments, return values, examples and mutation behavior. Existing getters used by the student receive concise English explanations where necessary. Ordinary string equality, numeric comparisons and collection operations stay visible as learning goals rather than being hidden in helpers.

## Summary behavior and API

Extend the existing GET /api/animals response with one additive summary object. Compute it from the exact list remaining after the existing filters, never from the full Catalog again. No second endpoint or extra browser request is required.

```json
{
  "items": [],
  "total": 0,
  "summary": {
    "shelterCount": null,
    "speciesCounts": null,
    "unfinishedLessons": ["lesson-6", "lesson-7"]
  }
}
```

- A completed shelter exercise returns a nonnegative integer, counting unique shelter IDs even when several animals share one shelter.
- A completed species exercise returns both DOG and CAT keys, with integer counts. Empty results produce zero for both keys, not missing keys.
- In the example above, null means an unfinished exercise; it never means zero animals or zero shelters.
- Evaluate lessons 6 and 7 independently. Catch only the intentional ExerciseNotImplementedException at this boundary and record that lesson ID. One unfinished summary method must not suppress the other result or the catalog.
- Existing filter stubs still return the existing 501 response when their selected filter is unfinished. Existing invalid-input and missing-ID behavior stays unchanged.
- Unexpected Java bugs still use the existing sanitized 500 response and server-side stack trace. This change does not disguise arbitrary bugs as unfinished lessons.
- The frontend validates the new payload shape and distinguishes unavailable metrics from real zero values.

## Frontend learning boundary

The provided catalog component renders total animal count, distinct shelter count and DOG/CAT counts. Available values are displayed even if another summary exercise is unfinished. A compact learning note identifies only missing summary lessons; the existing cards and official shelter links remain available. When a search starts or fails, old summary values disappear together with old results. The existing latest-request guard covers the entire response.

**Builder proposal:** summary rendering itself is provided so completing either Java summary method produces visible progress immediately. Student Python work concentrates on producing the opportunity-label list, rather than blocking the visibility of Java progress behind another stub.

Refactor the lesson-5 entry point into a small pure function that accepts one validated animal dictionary and returns a list of display strings. Numbered English comments explain reading adoptable/fosterable, independently adding the corresponding fictional-data labels, handling both/neither, and returning the list. UI components, CSS, callbacks, networking and exception handling are provided helpers. Label text must describe mock opportunities, not a real animal's availability.

The provided renderer displays those labels on catalog cards and the optional detail page. A known unfinished lesson produces a compact page-level notice instead of twenty repeated warning boxes. Do not catch arbitrary Python bugs as lesson incompletion or silently replace the student's function with a completed solution. A local preview/test fixture allows all four boolean combinations to be checked without implementing Java ID lookup or modifying catalog data.

## Verification and documentation

- Baseline Java tests cover summary wiring with test doubles: exact filtered input, independently unfinished metrics, real zero results, and existing API behavior. Helpers have focused tests for meaningful normalization/array-copy behavior.
- Opt-in exercise tests describe the new HashSet/HashMap contracts, including repeated shelter IDs, empty input, both species, a missing species, and unchanged input. They intentionally fail on the supplied stubs.
- Python baseline tests cover summary validation, partial rendering state, stale-response protection and isolated known lesson incompletion. Tests must not require a student's function to stay unfinished.
- Separate opt-in Python exercise checks cover all four adoptable/fosterable combinations and list output without requiring browser setup or Java ID lookup. Keep the existing baseline command usable.
- Browser checks cover initial cards with missing-summary notes, no stale statistics during refresh/error, working official links, and responsive placement. Completed-result rendering can be checked with test doubles/isolated fixtures; do not temporarily ship answer implementations.
- Update README, API docs, student guide and teacher guide together. All new/changed source comments and docstrings are English; existing Korean guides may remain Korean.
- Guides identify student-owned files, provided helper APIs, lesson-specific check commands and expected intentional failures. Avoid tying success to fixed total test counts when tests evolve.

## Decisions left to implementation

Choose the smallest helper placement and UI styling consistent with the existing files. Keep summary integration close to the current controller and page; use no generic extension mechanism. Small test doubles/fixtures are allowed only for wiring verification, not as completed student answers. No lesson progress tracking or new user settings are needed.

## Acceptance

The unchanged unfiltered catalog and shelter links work with all new stubs unfinished. Each Java summary exercise can be completed and observed independently. Students can solve every assigned collection operation using the named beginner concepts. English step comments and helper documentation are present at the edit sites. Framework checks pass while opt-in acceptance checks accurately report unfinished student work.

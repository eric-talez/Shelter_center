# Search Summary Milestones Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Complete summary/helper/UI plumbing while leaving beginner collection and label exercises as English, step-guided stubs.

**Architecture:** Add independently optional summary values to the existing catalog response, calculated through student methods on the filtered list. Keep Java collection exercises in one file and Python label exercises in a pure module; small concrete helpers handle normalization, species codes, and UI rendering. No new endpoint or stateful subsystem.

**Tech Stack:** Existing Java 21/Spring Boot/Maven Wrapper and Python 3.13/NiceGUI/httpx/pytest; no dependencies added.

**Spec:** `docs/superpowers/specs/2026-11-05-summary-milestones-design.md`

## Global Constraints

- Preserve the existing stack, fixtures, official links, mock-data notices and local run workflow.
- Keep the existing five Java exercise methods unfinished and improve their instructions.
- Add two Java exercise stubs for distinct shelter counts and species counts.
- Existing lesson IDs remain stable, avoiding changes to existing test selectors and links.
- Students must preserve the input list and order where applicable. Collections are local to each call. No streams, lambdas, advanced generic abstractions or Spring knowledge are needed in student implementations.
- Supply small concrete helpers; do not add repositories, service interfaces, persistence, authentication, new dependencies or a generalized lesson engine.
- Do not add a shelter filter, real animal ingestion, favorites, deployment, completed student solutions or automatic solution fallback.
- All new/changed source comments and docstrings are English; existing Korean guides may remain Korean.
- Summary rendering itself is provided so completing either Java summary method produces visible progress immediately. Student Python work concentrates on producing the opportunity-label list.

## Review Focus

- One completed summary lesson must remain visible when the other is unfinished (Tasks 1 and 2 partial-summary tests).
- Empty results show real zeros only for completed lessons; unfinished values remain null (Tasks 1 and 2 empty/zero tests).
- Old requests, loading states and errors must not leave stale summary values onscreen (Task 2 state tests).
- Programming errors must not be swallowed as lesson incompletion (Tasks 1 and 2 unexpected-error tests).
- Completing student methods must not break baseline tests that accidentally require stubs (Tasks 1 and 2 injected-double tests).

## File Map

- Java: `AnimalExercises.java` is student-owned; new `ExerciseHelpers.java` supplies only normalization/species arrays; `Animal.java` explains getters; `AnimalController.java` owns summary wiring.
- Java tests: existing controller/exercise tests plus focused `ExerciseHelpersTest.java`.
- Python: `student_ui.py` contains the pure label stub; new `ui_helpers.py` owns rendering and the intentional-exception boundary; `app.py` integrates both; `api_client.py` validates summary.
- Python tests: baseline under `frontend/tests`; opt-in student tests under `frontend/exercises`.
- Docs: Task 1 updates `docs/api.md`; Task 2 updates README and student/teacher guides for the complete workflow.

### Task 1: Java summaries and guided collection stubs

**Files:** Modify `backend/src/main/java/org/royshelter/{AnimalExercises,Animal,AnimalController}.java`, `backend/src/test/java/org/royshelter/{AnimalControllerTest,AnimalExercisesTest}.java`, `docs/api.md`; create `backend/src/main/java/org/royshelter/ExerciseHelpers.java` and `backend/src/test/java/org/royshelter/ExerciseHelpersTest.java`.

**Interfaces:**
- Preserve all existing exercise signatures/lesson IDs. Add `public int countDistinctShelters(ArrayList<Animal> animals)` (lesson-6), `public HashMap<String, Integer> countBySpecies(ArrayList<Animal> animals)` (lesson-7).
- `ExerciseHelpers.normalizeText(String text): String` trims and lowercases using Locale.ROOT; input is non-null. `ExerciseHelpers.supportedSpecies(): String[]` returns a fresh DOG/CAT array. Both are public static, with English contracts/examples.
- GET `/api/animals` keeps items/total and adds `summary: {shelterCount: Integer|null, speciesCounts: {DOG:int,CAT:int}|null, unfinishedLessons: string[]}`. Missing lesson IDs are lesson-6 then lesson-7. Evaluate independently on the exact filtered ArrayList. Catch only ExerciseNotImplementedException; ordinary exceptions retain current 500/logging. Null-capable record or ordinary map is fine; do not use Map.of for nullable values.

- [ ] **Step 1: Write baseline failing tests for API/helper behavior.** Use fixed-return exercise doubles, not completed solution algorithms, and never require the real stubs to remain unfinished. Assert these concrete cases:
  - Both summary doubles throw their lesson exceptions: status 200, 20 items, both metric fields null, unfinishedLessons exactly [lesson-6,lesson-7].
  - Each partial combination returns the completed metric and only the missing lesson ID. Use shelterCount=1 and counts DOG=1,CAT=0 for a fixed one-animal filter result; assert both methods receive that same filtered list.
  - Completed empty result gives total=0,shelterCount=0,DOG=0,CAT=0,unfinishedLessons=[].
  - Unexpected summary IllegalStateException gives INTERNAL_ERROR 500 rather than an unfinished summary. Existing diagnostics remain intact.
  - Helper assertions: `normalizeText("  MIX  ") == "mix"`, empty whitespace becomes empty, uppercase I normalizes to i even under Turkish default locale (restore locale), changing a returned species array does not change a subsequent one.
- [ ] **Step 2: Run focused baseline tests and record expected RED evidence.** Command uses `env JAVA_HOME="$PWD/.tools/jdk-21.0.13+9/Contents/Home" backend/mvnw -B -ntp -f backend/pom.xml test`. Missing API/helper behavior must be identified before implementation.
- [ ] **Step 3: Implement helpers and summary plumbing, leave seven student methods unfinished.** Give every student method an English purpose/input/output/example, Step 1–4 comments describing collection preparation, inspection, processing and return, edge cases, and removal of the final throw when complete. Give hints without executable loop/map/set mutations or full predicates. Explain getters the exercises use. Only the two named peripheral helpers are needed; no collection-answer helpers. Update API docs with partial/zero/complete summary and error behavior.
- [ ] **Step 4: Add Lesson6/Lesson7 opt-in acceptance tests.** Use existing three animals: sfacc/phs/sfacc gives 2 unique shelters and DOG=2,CAT=1; empty input gives 0 and both zero keys; dogs-only includes CAT=0; source list stays unchanged. Exercise checks specify output, not a source-text implementation. Preserve existing Lesson1–4 tests and selectors.
- [ ] **Step 5: Verify and commit.** Baseline Java suite passes; selected Lesson6/Lesson7 tests fail only at their intentional stub exceptions. Run `git diff --check`. Commit task files; report precise RED/GREEN commands, counts, expected exercise failures, contracts, and limitations. Do not run/restart servers. No Python edits in this task.

### Task 2: Python summary integration, label exercise and learning handoff

**Files:** Modify `frontend/{api_client,app,student_ui}.py`, `frontend/tests/{test_api_client,test_ui_state}.py`, `README.md`, `docs/{student-guide,teacher-guide}.md`; create `frontend/ui_helpers.py`, `frontend/tests/test_ui_helpers.py`, `frontend/exercises/test_student_ui.py`.

**Interfaces:**
- Consume Task 1 summary payload exactly as specified above. `refresh_catalog` page state gains `summary`, clearing it to None at request start and on errors; the latest-request guard covers it.
- `student_ui.opportunity_labels(animal: dict) -> list[str]` is pure, with no NiceGUI imports, and raises `student_ui.ExerciseNotImplementedError` until completed. The exception identifies lesson-5. Exact eventual labels are `Mock adoption option` then `Mock foster option`; both flags false returns []. Keep this algorithm a stub.
- In `ui_helpers.py`, provide `get_opportunity_labels(animal: dict) -> list[str] | None`, catching only the dedicated student exception; None means unfinished. Wrong output type or ordinary bugs are visible programming errors, not unfinished lessons. Provide `render_summary(summary: dict) -> None` and `render_opportunity_badges(labels: list[str]) -> None` with English helper docstrings. The latter renders supplied strings; it does not decide which labels to produce.
- The app owns one compact lesson-5 notice per page when relevant, renders supplied badges in catalog/detail, and preserves all official links. Summary renderer displays available shelter/DOG/CAT values and a compact note for missing summary lessons. No extra API request or new production route.

- [ ] **Step 1: Write focused failing tests.** Update success fixtures to the additive summary contract. Assert partial combinations, complete zeros, and invalid summary values (missing fields, bool/negative counts, missing DOG/CAT, inconsistent null/lesson IDs) become INVALID_RESPONSE. Assert stale completion cannot replace latest summary, and loading/error clears previous summary. Test helpers with injected completed/unfinished/broken student functions: real supplied strings render, only dedicated exception becomes None, and ordinary errors propagate. Use concrete NiceGUI element inspection or a small capture double to verify summary text/zero/missing notes and badge text; no test may require the real student function to stay unfinished.
- [ ] **Step 2: Run the focused tests to record RED before implementation.** Use `.venv/bin/python -m pytest frontend/tests -q`, narrowing selectors while iterating.
- [ ] **Step 3: Implement API validation, page wiring, rendering helpers and pure lesson-5 stub.** Explain input dictionary, independent booleans, list construction, Step 1–4, both/neither examples, helper purpose and throw-removal in English. Remove the old NiceGUI-dependent student implementation and update every caller. The provided helpers must not contain the completed flag-to-label algorithm. Preserve latest-request handling and clear stale summary. Avoid repeated notices on every card. Keep current responsive layout and direct shelter navigation.
- [ ] **Step 4: Add separate opt-in label acceptance tests with four local dictionary fixtures.** Parameterize (True,False)->[Mock adoption option], (False,True)->[Mock foster option], (True,True)->both in that order, (False,False)->[]. Assert list type and unchanged input. The checks run without Java, HTTP or browser and fail at the deliberate stub. Keep them outside `frontend/tests` so the documented baseline command stays green.
- [ ] **Step 5: Update learning/run docs.** Include Java lessons 6/7, array initialization practice, exact helper APIs/examples, all student-owned edit sites, independent summary availability, optional ID lookup, and Python exercise command `.venv/bin/python -m pytest frontend/exercises -q`. Remove claims that HashSet is unused, Python exercise requires lesson-3, or Python acceptance is manual-only. Document recommended order (1,2,4,6,7,5; optional 3), restarting servers, zero versus unfinished, and no reference answers. Replace stale fixed test totals with descriptions/actual command output guidance. Explain baseline versus opt-in acceptance commands on macOS/Linux and Windows using existing conventions.
- [ ] **Step 6: Verify and commit.** Baseline Python suite passes; the four opt-in label cases fail at the intentional stub. Run Java baseline once for final cross-language verification and git diff check. Commit task files and report precise RED/GREEN evidence, resulting interfaces, documentation checks and any browser items the controller must verify. Do not manipulate running servers. Controller owns packaging/live browser verification after commit.

## Controller final verification

- Package Java; run baseline suites if final code changed since task verification; confirm intentionally unfinished exercise suites fail at the correct lessons.
- Start/update owned local servers, smoke real API payload and unchanged links.
- Browser: initial 20 cards with partial-learning notes, lesson-5 note once, filters still report their lesson, Reset restores catalog, responsive width has no overflow, existing outage/retry clears old summary. Completed rendering is covered by isolated baseline fixtures, not student answers.
- Whole-change review and scoped fix review if required. Preserve local branch/workspace; no merge or deployment. Existing PR publishing can be handled after implementation under session authorization.

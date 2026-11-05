# Shelter Learning Starter Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Deliver a runnable Java/Python shelter demo whose collection-based search implementations remain clearly identified student exercises.

**Architecture:** Spring Boot loads fixed JSON fixtures and serves a read-only API; its controller validates inputs and calls plain Java exercise methods. NiceGUI requests that API asynchronously and renders English browsing screens. No database, repository abstraction, service-interface hierarchy, authentication, or deployment infrastructure.

**Tech Stack:** JDK 21, Spring Boot 4.1.2, Maven Wrapper with Maven 3.9.11; Python 3.13, NiceGUI 4.0.0, httpx 0.28.1, pytest 9.2.0. Official Spring Initializr/PyPI metadata checked before pinning. Resolve packages from official sources, never disable TLS verification.

**Spec:** `docs/superpowers/specs/2026-11-04-shelter-learning-starter-design.md`

## Global Constraints

- 백엔드는 Java.
- 학생의 학습 경험을 고려해 화면도 최대한 Python으로 작성.
- 실제 북부 캘리포니아 보호소 이름을 사용하고, 동물은 mock data로 구성.
- 사이트는 정보 탐색과 보호소 연결을 담당. 입양/foster 신청은 보호소에서 진행.
- 어려운 구조와 연결은 우리가 만들고, 세부 기능은 학생이 공부하며 구현.
- 검색·조회 규칙은 Java에 둔다. Python은 입력값을 보내고 결과를 표시한다.
- 학생이 초기에 수정하는 Java 파일은 1~2개 이내로 제한한다.
- 기본 풀이에는 for문, if문, 이미 배운 컬렉션을 사용한다. Stream, 람다, 디자인 패턴을 필수로 요구하지 않는다.
- 전체 화면과 상세 정보에 가상 동물이라는 안내를 유지한다. 보호소와 제휴했다고 표현하지 않는다.
- 클라우드 배포와 외부 공개. 우선 로컬에서 실행하고 학습한다. (Spec §4: these are excluded; local only.)
- 학생 과제의 정답 구현. 요청이 있으면 나중에 별도 교사용 자료로 작성한다. (Spec §4: excluded.)

## Review Focus

- Invalid query values must return 400 before any unfinished exercise is invoked (Task 1 API test).
- An unfinished exercise, no matches, unknown ID, and backend outage must remain distinct outcomes (Tasks 1 and 2 tests).
- Java filtering must never run twice in Python, and concurrent refreshes must not replace current results with old ones (Task 2 request/render test).
- Fixtures must not imply real animal availability or invented shelter locations; contact URLs must point to verified official pages (Task 1 data integrity and Task 2 display checks).
- Default verification must pass while opt-in student tests fail meaningfully on unfinished methods, then be usable independently by lesson (Tasks 1 and 3 command verification).

---

## File Map

- `backend/pom.xml`, Maven wrapper, `src/main/resources/application.properties`: reproducible build and localhost server.
- `backend/src/main/java/org/royshelter/{ShelterApplication,Animal,Shelter,Catalog,AnimalExercises,ExerciseNotImplementedException,AnimalController,ApiErrors}.java`: boot, simple data objects, fixture loading, student code, and HTTP boundary. No additional service/repository layers.
- `backend/src/main/resources/data/{animals,shelters}.json`: fixed source data.
- `backend/src/test/java/org/royshelter/{CatalogTest,AnimalControllerTest,AnimalExercisesTest}.java`: starter integrity/API and separate tagged exercise tests.
- `frontend/{app,api_client,student_ui}.py`, `frontend/requirements.txt`: UI, transport, optional Python exercise.
- `frontend/assets/{dog,cat,placeholder}.svg`: original local demo illustrations, code-native SVG.
- `frontend/tests/{test_api_client,test_ui_state}.py`: HTTP and UI orchestration regression cases.
- `README.md`, `docs/{student-guide,teacher-guide,api,data-sources}.md`: Korean teaching/operating instructions and exact contracts.

### Task 1: Java catalog, API boundaries, and collection exercises

**Files:** Create the backend files in the File Map, `.mvn/wrapper` and `mvnw`/`mvnw.cmd` inside backend, and `docs/data-sources.md`. Root `.gitignore` already exists; extend only for generated artifacts if necessary.

**Interfaces:**
- Produces `Animal` with constructor/getters for `String id,name,species,breed`, `int ageMonths`, `String description,imageUrl,shelterId`, `boolean adoptable,fosterable,demo`. Provide familiar getters (`getName`, `isAdoptable`) rather than requiring students to learn records/Lombok. `Shelter` JSON fields: `id,name,city,websiteUrl,phone,sourceUrl`.
- Produces `Catalog.animals(): ArrayList<Animal>` and `Catalog.shelters(): ArrayList<Shelter>`; protect the stored list against request/student mutation with copies. Models expose no setters. The catalog validates unique IDs, existing shelter references, species, nonnegative ages, demo=true, official HTTPS URLs and image paths at startup.
- Produces `AnimalExercises.filterBySpecies(ArrayList<Animal>, String): ArrayList<Animal>` (lesson-1), `filterByMaxAge(ArrayList<Animal>, int): ArrayList<Animal>` (lesson-2), `findById(ArrayList<Animal>, String): Animal` (lesson-3), `filterByShelters(ArrayList<Animal>, HashSet<String>): ArrayList<Animal>` (lesson-4), `filterByQuery(ArrayList<Animal>, String): ArrayList<Animal>` and `filterByPurpose(ArrayList<Animal>, String): ArrayList<Animal>` (both lesson-5). All are plain public instance methods, each throws `new ExerciseNotImplementedException("lesson-N")` with a Korean TODO. No answer implementation.
- Produces `GET /api/animals` -> `{items: Animal[], total: int}`; `GET /api/shelters` -> `{items: Shelter[]}`; `GET /api/animals/{id}` -> one Animal object. No nested shelter in Animal; clients join by shelterId.
- Query contract: species DOG/CAT, maxAgeMonths nonnegative integer, repeated shelterId known IDs, trimmed q (empty omitted), purpose ADOPT/FOSTER. Unknown parameters, repeated singleton parameters and invalid enum/number/shelter IDs -> 400. Omitted/empty selection applies no filter. Validate all input before invoking exercise methods; sequential AND across active kinds, order species/age/shelter/query/purpose. No pagination. Preserve fixture order.
- Errors: `{code: string, message: string, lessonId: string|null}`. 400 `INVALID_REQUEST`, 404 `NOT_FOUND`, 501 `EXERCISE_NOT_IMPLEMENTED`, 500 `INTERNAL_ERROR`; never leak stack traces or classify arbitrary exceptions as unfinished lessons. Lesson exceptions are the only 501 source. Lookup null maps to 404; empty search list maps to 200.
- Bind to `127.0.0.1:8080`; optional `SERVER_PORT` retains standard Spring override. Plain Spring Web + test dependencies only.

- [x] **Step 1: Prepare local runtime and write the failing catalog/API tests.**
  Obtain JDK 21 locally under ignored `.tools/` if unavailable, using official vendor download with checksum; do not install globally. Generate official Maven Wrapper with pinned distribution. Configure starter tests excluded tag `exercise` by default, and Maven profile `exercises` to include only that tag. Bootstrap the build to make the real tests runnable, then establish RED before product code.
  Fixture test asserts 20 animals, 5 shelters, exactly 4 animals per shelter, valid references/unique IDs/demo flags, DOG and CAT present and all three combinations of adoptable/fosterable (excluding both false). Pin first item `mock-sfacc-01`, name `Luna`, DOG, breed `Terrier mix`, ageMonths 24, shelterId sfacc, adoptable true, fosterable false, imageUrl `/assets/dog.svg`, demo true. Other deterministic values are implementer choice. All images are `/assets/dog.svg`, `/assets/cat.svg`, or empty.
  API tests use actual Spring MVC request mapping with real catalog and substitute exercise object only where a completed lesson result is required. Assertions include:
  ```java
  // Named tests with concrete outcomes (use MockMvc or equivalent Spring API):
  // listsCatalogWithoutExercises: GET /api/animals => 200, total=20, first id=mock-sfacc-01
  // validatesBeforeExercise: ?species=DOG&maxAgeMonths=-1 => 400 INVALID_REQUEST
  // rejectsMalformedQueries: age=abc (using maxAgeMonths), unknown param, unknown shelter,
  // species=BIRD, purpose=OTHER, repeated species => 400 INVALID_REQUEST
  // reportsUnfinishedLesson: ?species=CAT => 501 EXERCISE_NOT_IMPLEMENTED lesson-1
  // emptyMatchesAreSuccess: injected filter returns [] => 200, items=[], total=0
  // absentAnimalIsNotFound: injected findById returns null => 404 NOT_FOUND
  // completedDetailUsesLookup: injected findById returns Luna => 200 and id=mock-sfacc-01
  // unexpectedFailureIsNotLesson: injected method throws IllegalStateException => 500 INTERNAL_ERROR
  // whitespaceQueryIsUnfiltered: ?q=%20%20 => 200, total=20
  ```
- [x] **Step 2: Run RED.** `cd backend && ./mvnw test`; capture specific missing catalog/API failures, not dependency or tool failure.
- [x] **Step 3: Implement the fixtures, catalog, routes and error mapping using the interfaces above.**
  Verify official general contact info from sources in spec: sfacc +1-415-554-6364; phs +1-650-340-7022; hssv +1-408-262-2133; svaca phone must be looked up on official site; sjacs +1-408-794-7297. Preserve actual cities. Use official HTTPS general adoption URLs and record verification date/source in docs/data-sources.md. Animal profiles are invented and friendly, with no real individual adoption links or urgency claims.
- [x] **Step 4: Add tagged lesson acceptance tests.** `AnimalExercisesTest` contains nested `Lesson1`..`Lesson5` groups. Build a small fixture with 0,24,25-month animals across two shelters. Assert species match/no match/original list intact; inclusive age boundary/zero; map existing/missing ID; shelter OR/empty selection/source order; trimmed case-insensitive substring across name OR breed, empty q gives all; ADOPT/FOSTER booleans independently. Tests call actual unfinished methods and fail with the named lesson exception. Tests must not demand the stubs remain unfinished after a student completes them; starter tests simulate the exception via an injected test double instead of asserting the real stubs throw.
- [x] **Step 5: Verify GREEN and the teaching failure.** `./mvnw test` and `./mvnw package` pass; `./mvnw -Pexercises '-Dtest=AnimalExercisesTest$Lesson1' test` fails specifically with lesson-1. Configure a clear selector if nested selection needs adaptation and document the exact verified command. Do not implement answers to turn lesson tests green.
- [x] **Step 6: Commit backend and source documentation.** `git add backend docs/data-sources.md .gitignore` then `git commit -m "feat: add Java shelter API and collection exercise seams"`.

### Task 2: Python browsing UI with explicit teaching states

**Files:** Create `frontend/app.py`, `frontend/api_client.py`, `frontend/student_ui.py`, `frontend/requirements.txt`, `frontend/assets/dog.svg`, `frontend/assets/cat.svg`, `frontend/assets/placeholder.svg`, `frontend/tests/test_api_client.py`, `frontend/tests/test_ui_state.py`.

**Interfaces:**
- Consumes Task 1 HTTP contract above: lists have items, animals additionally total; detail is a single Animal; errors have code/message/lessonId. Animal fields and images match JSON names exactly. Use paths /api/animals and /api/shelters, no Python data copies/filters.
- Produces async `ShelterApi.animals(params: list[tuple[str,str]]|None=None) -> dict`, `ShelterApi.shelters() -> list[dict]`, `ShelterApi.animal(animal_id: str) -> dict`; constructor accepts an httpx.AsyncClient for tests/app lifetime. Raise `ApiError(code: str, message: str, lesson_id: str|None=None)` for declared HTTP failures, connection failure (`BACKEND_UNAVAILABLE`), timeout (`BACKEND_TIMEOUT`) or invalid successful response (`INVALID_RESPONSE`). Use standard URL encoding and httpx timeout 5 seconds.
- Produces `student_ui.render_opportunities(animal: dict) -> None`: optional lesson-5 placeholder rendered as an explicit learning note, no answer implementation. Baseline detail content remains useful when it is unfinished.
- Start `python frontend/app.py`, local UI `127.0.0.1:8081`, `SHELTER_API_URL` default `http://127.0.0.1:8080`. `FRONTEND_PORT` optional. Importing UI module for testing must not start the server; main guard. Assets served locally.

- [x] **Step 1: Write failing client tests with httpx.MockTransport.** Assert success shapes, species and maxAgeMonths=0 encoded correctly; known 501 preserves lesson-1; 404 remains NOT_FOUND; 500 remains server error; ConnectError/TimeoutException are distinct; malformed/non-JSON success is INVALID_RESPONSE. Use `asyncio.run` so no pytest-asyncio dependency is needed. Add an orchestration test proving older request completion cannot overwrite a newer selection and that a failed refresh does not leave old results presented as current matches.
- [x] **Step 2: Run RED.** `python3 -m venv .venv`, install pinned frontend requirements, `.venv/bin/python -m pytest frontend/tests -q`; expect missing implementation failures, then implement.
- [x] **Step 3: Implement api_client.py and the NiceGUI app.**
  Warm cream/forest green simple responsive layout, visible title `Bay Area Shelter Hub`, subtitle explaining local discovery. Persistent visible copy: `Learning demo — all animal profiles are fictional. Shelter names and links are real; this project is not affiliated with them.` English controls: species, maximum age in months, search name/breed, purpose; Search and Reset. Label features as learning exercises so a 501 is expected and helpful. Whole catalog automatically loads; show true count, local original SVG illustrations or placeholder, name/species/age, real shelter name/city and Demo label. For filter submit, preserve numeric zero. Inputs remain visible during loading and after errors; disable submission during a fetch or use a small request-generation guard, keeping state per client/page. Do not add a generalized state framework.
  Animal name and primary card CTA link directly to the matched shelter websiteUrl; do not show internal detail links in the main catalog. An optional `/animals/{id}` learning route attempts Java lookup and visibly explains lesson-3 when unfinished; 404 and offline are distinct with a back link. Successful detail renders description, real shelter info and `Visit shelter website`/general phone links plus explicit fictional-profile notice; calls student_ui.render_opportunities. The primary catalog flow must work without completing any exercise. Keep shelter name/city visible, with no shelter selector. Links must clearly refer to real shelters, not adoption of the fictional animal. Escape content through NiceGUI text APIs. Use accessible labels, meaningful image alt, readable contrast and keyboard reachable links.
  Empty result: `No animals match these filters.` Offline: actionable start-Java/retry message; unfinished: lesson ID and path to AnimalExercises.java. Invalid query: correct the input. Never collapse these states or hide exceptions behind an empty list. Avoid excessive infrastructure jargon outside the explicitly educational notices.
- [x] **Step 4: Verify tests and browser behavior.** Run frontend pytest, start both local servers, manually verify baseline 20 cards, species -> lesson-1, Reset -> 20, direct detail -> lesson-3, max age 0 reaches backend, official shelter links, missing images fallback and mobile layout. Stop Java once to verify a visible connection failure and retry after restarting. Use available browser tool; if unavailable, report that specific verification limit and perform HTTP/local UI tests.
- [x] **Step 5: Commit.** `git add frontend` then `git commit -m "feat: connect Python shelter browser to learning API"`.


Scope update approved during implementation: remove shelter filtering; keyword/purpose become lesson-4 and Python opportunity display lesson-5. Task 3 removes the now-unused backend shelter filter and aligns those lesson IDs. Before that cleanup, Task 2 may temporarily receive lesson-5 from existing backend keyword/purpose stubs.

### Task 3: Align simplified exercise scope and deliver student/tutor guides

**Files:** Create `README.md`, `docs/student-guide.md`, `docs/teacher-guide.md`, `docs/api.md`. Modify `backend/src/main/java/org/royshelter/AnimalExercises.java`, `AnimalController.java`, and the corresponding `AnimalExercisesTest.java` and `AnimalControllerTest.java` to remove shelter filtering and renumber keyword/purpose lessons. Keep the optional detail lookup API and the remaining contracts unchanged.

**Interfaces:** Consumes exact methods, lesson IDs, API shapes, run/test commands from Tasks 1 and 2. Produces complete Korean instructions for a beginner to start both servers, complete one lesson at a time, and understand what default verification means.

- [x] **Step 0a: Specify the approved scope change in tests.** Unknown `shelterId` must now return 400 as an unsupported query. Remove shelter-filter expectations and the old HashSet exercise group; rename keyword/purpose acceptance group to Lesson4. Assert lesson-4 for keyword/purpose exception mapping using the same injected unfinished double pattern. Run the focused controller tests and capture the expected failure before changing routes.
- [x] **Step 0b: Remove the unused backend filter seam.** Delete `filterByShelters`, shelterId handling/validation and the shelter stage in composition; query and purpose stubs throw lesson-4. Keep 1/2/3 intact. Do not replace the removed HashSet exercise with another unrequested product feature. Verify default backend tests pass and Lesson4 acceptance tests intentionally fail with lesson-4.
- [x] **Step 1: Write README with prerequisites and literal commands.** Explain JDK 21 and Python 3.13, Maven Wrapper, venv creation/requirements, two terminals, localhost URLs, Ctrl-C shutdown, ports occupied and backend offline. Provide project-local JDK path setup if downloaded here without hardcoding this user's home directory into tracked files. Document Windows commands alongside POSIX where straightforward. No Docker, make prerequisite, global environment mutation or bespoke process supervisor. Explain that Java restarts after code changes and UI refresh applies the result.
- [x] **Step 2: Write student-guide.md.** Preparation reads an Animal name and loops over a supplied list. Each lesson has file/method, expected input/output, one concrete example, tiered conceptual hints (not answers), verification command and common mistake. Use for/if/ArrayList/HashMap. Explain that HashSet was learned already but is not required by this product. Explain `.equals` versus `==`, inclusive comparison, `HashMap.get` returning null and source-list mutation in the relevant lesson. Introduce HTTP/JSON only after initial Java exercises. Python lesson-5 describes dictionary reads and conditional labels without solved code. No complete answer in docs, comments, tests or reference modules.
- [x] **Step 3: Write teacher-guide.md and api.md.** Teacher guide maps goals, prerequisites, first guided lesson and observation questions; tests per lesson and baseline checks distinguished; no fixed course duration. API doc specifies species/maxAgeMonths/q/purpose only, exact parameter validation, list/detail/error examples, demo provenance and statuses 200/400/404/501/500 plus client outage. Explain how new data can replace fixture contents later without building future adapters now.
- [x] **Step 4: Verify the written commands once against final code.** Default backend tests/package and frontend tests pass. Run one exercise command and observe expected lesson failure. Check a baseline live API request and UI launch using README paths; verify sources and code links resolve. Check no student solution, secrets, build artifacts or downloaded toolchains are tracked. `git diff --check` passes. Capture exact counts/results for final review.
- [x] **Step 5: Commit.** `git add README.md docs backend frontend` for only task-owned changes then `git commit -m "feat: simplify learning scope and add student run guides"`.

## Execution Rulings

- The user approved the design and explicitly requested implementation with subagents; continue through plan execution without another plan-approval pause.
- The workspace was a dedicated new project folder with only its design document and no repository. Initialize a new Git repository on `feat/shelter-learning-starter` in this folder; no existing main/master or user code is modified. No additional checkout is needed to protect an existing branch.
- Keep runtime downloads in ignored `.tools`, Python environment in ignored `.venv`, logs in ignored `.runtime`. Do not change system-wide JDK/Python settings.
- This plan is one runnable learning project. Its tasks are sequential integration boundaries, not parallel implementations; a fresh implementer and review is used for each.

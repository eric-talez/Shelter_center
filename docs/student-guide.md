# 학생 가이드

먼저 [README 실행 순서](../README.md)를 따라 전체 목록을 보세요. 모든 보호소의 가상 동물을 모아서 보여주고, 동물을 고르면 실제 보호소 사이트로 연결됩니다. 실제 동물이나 현재 입양 가능 여부를 표시하지 않습니다.

권장 순서는 준비 → 1 → 2 → 4 → 6 → 7 → 5이며 수업 3(ID 조회)은 선택입니다. 학생이 수정할 곳은 [AnimalExercises.java](../backend/src/main/java/org/royshelter/AnimalExercises.java)의 `filterBySpecies`, `filterByMaxAge`, `filterByQuery`, `filterByPurpose`, `countDistinctShelters`, `countBySpecies`, 선택 `findById`와 [student_ui.py](../frontend/student_ui.py)의 `opportunity_labels`입니다. 서버 연결, URL 처리, 화면 전체 구조는 제공됩니다. `for`, `if`, `ArrayList`, `HashSet`, `HashMap`, 배열로 충분히 풀 수 있으며 Stream, 람다, Spring 지식은 필요하지 않습니다. 컬렉션은 호출마다 새로 만들고 입력 목록·딕셔너리와 순서를 보존하세요. 정답 구현은 제공하지 않습니다.

## 준비 연습 — 동물 한 마리와 목록 읽기

[Animal.java](../backend/src/main/java/org/royshelter/Animal.java)에서 `getName()`, `getSpecies()`, `getAgeMonths()`가 어떤 값을 읽는지 찾아보세요. `Catalog.animals()`는 공급된 목록의 복사본을 돌려줍니다. 처음부터 생성자나 파일 읽기 코드를 작성할 필요가 없습니다.

교사와 편집기의 Java 디버거에서 [수업 테스트](../backend/src/test/java/org/royshelter/AnimalExercisesTest.java)의 `matchesSpeciesWithoutChangingSource`에 중단점을 걸고 수업 1 명령을 디버그 실행해도 됩니다. 테스트가 제공하는 `source`에는 순서대로 Luna(DOG, 0개월), Milo(CAT, 24개월), Poppy(DOG, 25개월)가 있습니다. 처음에는 TODO 예외에서 멈추거나 실패하는 것이 정상입니다. 별도 연습장 또는 디버거에서 아래 두 목표만 먼저 연습하세요. 이 준비 연습을 제품 메서드의 정답으로 붙여 넣지 마세요.

- 동물 한 마리를 받아 이름을 읽기: Luna를 읽었으면 예상 값은 문자열 `Luna`입니다.
- 공급된 목록을 `for`로 순회하며 각 이름을 확인하기: 예상 순서는 Luna → Milo → Poppy입니다. 필터링은 아직 하지 않습니다.

힌트 1: 목록과 목록 안의 한 마리는 자료형이 다릅니다. 힌트 2: 메서드 이름이 `get`으로 시작하면 값을 읽는지 확인하세요. 흔한 실수는 한 마리의 이름 대신 목록 전체를 이름처럼 다루는 것입니다. 검증은 교사에게 세 이름과 순서를 설명하고 아래 수업 1 테스트를 실행해 아직 실패하는 지점이 TODO인지 확인하는 것입니다.

## 수업 1 — 종류로 고르기 (`lesson-1`)

수정 위치: `AnimalExercises.filterBySpecies(ArrayList<Animal> animals, String species)`.

입력은 동물 목록과 `DOG` 또는 `CAT`입니다. 출력은 종류가 일치하는 동물의 **새 `ArrayList<Animal>`**입니다. 원래 순서를 유지하고 입력 목록을 수정하지 않습니다. 일치하는 동물이 없으면 빈 목록입니다. 테스트의 세 동물과 `DOG`를 받으면 Luna, Poppy가 남습니다.

힌트 1: 먼저 어떤 목록을 읽고 어떤 목록을 반환할지 구분하세요. 힌트 2: 한 마리의 종류와 선택한 종류가 같은지 물어보세요. 힌트 3: 문자열의 내용 비교는 `.equals(...)`입니다. `==`는 두 변수가 같은 객체를 가리키는지 검사하므로 내용이 같아도 틀릴 수 있습니다.

흔한 실수: 순회하면서 원본에서 동물을 삭제하기. `ConcurrentModificationException`이 생기거나 다음 필터에 전달할 목록이 손상될 수 있습니다. 새 결과 목록을 생각하세요.

프로젝트 최상위, JDK 21을 선택한 터미널에서:

```sh
backend/mvnw -f backend/pom.xml -Pexercises '-Dtest=AnimalExercisesTest$Lesson1' test
```

통과 기준은 종류 선택과 입력 보존입니다. 아직 구현하지 않았다면 `lesson-1` 예외로 실패합니다.

## 수업 2 — 최대 나이 (`lesson-2`)

수정 위치: `AnimalExercises.filterByMaxAge(ArrayList<Animal> animals, int maxAgeMonths)`.

입력은 목록과 0 이상의 최대 개월 수입니다. 출력은 최대 나이 **이하**인 동물의 새 목록이며 순서를 유지합니다. 테스트 목록에서 최대 24개월이면 0개월 Luna와 24개월 Milo가 남고, 25개월 Poppy는 빠집니다. 최대 0개월도 유효합니다.

힌트 1: 사람 나이가 아닌 `ageMonths`를 읽습니다. 힌트 2: “이하”는 경계값 자체를 포함합니다. 힌트 3: 결과가 0마리인 경우도 정상적인 목록입니다.

흔한 실수: `<`만 사용해 정확히 24개월인 동물을 빼거나 0개월을 잘못된 값으로 취급하기. 이 메서드는 전달된 목록을 읽고 새 목록을 돌려줍니다.

```sh
backend/mvnw -f backend/pom.xml -Pexercises '-Dtest=AnimalExercisesTest$Lesson2' test
```

통과 기준은 경계값과 0개월을 올바르게 처리하는 것입니다.

## 수업 3 — ID로 한 마리 찾기 (`lesson-3`, 선택)

수정 위치: `AnimalExercises.findById(ArrayList<Animal> animals, String id)`.

입력은 동물 목록과 찾을 ID, 출력은 그 동물 하나 또는 없을 때 `null`입니다. `HashMap<String, Animal>`에서 ID를 키로 사용해 보세요. 테스트의 ID `b`는 Milo를 반환하고 `missing`은 `null`을 반환합니다. 새 동물을 만들어 반환하는 과제가 아닙니다.

힌트 1: 지도에서 주소로 장소를 찾듯, ID와 객체를 연결합니다. 힌트 2: `HashMap`의 키와 값이 각각 무엇인지 말해 보세요. 힌트 3: `get`은 없는 키에 대해 `null`을 돌려줄 수 있습니다. `null`에 `getName()`을 호출하면 어떻게 될지 생각해 보세요.

흔한 실수: 이름을 키로 저장하고 ID로 조회하기, 없는 ID를 “첫 번째 동물”로 대신 반환하기. 컨트롤러가 `null`을 404로 바꿔 주므로 여기서는 HTTP 코드를 만들 필요가 없습니다.

```sh
backend/mvnw -f backend/pom.xml -Pexercises '-Dtest=AnimalExercisesTest$Lesson3' test
```

통과 기준은 존재하는 ID와 없는 ID를 구분하는 것입니다. Java 재시작 후 [별도 연습 페이지](http://127.0.0.1:8081/animals/mock-sfacc-01)에서도 확인할 수 있습니다. 기본 목록의 동물 이름은 공식 보호소로 바로 연결되므로 이 수업을 풀지 않아도 주 탐색 경로가 동작합니다.

## 수업 4 — 검색어와 연결 목적 (`lesson-4`)

수정 위치: `AnimalExercises.filterByQuery(ArrayList<Animal> animals, String query)` 및 `filterByPurpose(ArrayList<Animal> animals, String purpose)`.

검색어는 앞뒤 공백과 영문 대소문자를 무시하고 **이름 또는 품종의 부분 문자열**과 비교합니다. 출력은 일치하는 동물의 새 목록이며 원래 순서를 유지합니다. 테스트 입력 `"  LuN  "`은 Luna, `"MIX"`는 Luna와 Poppy입니다. 공백뿐인 검색어는 동물 모두를 같은 순서로 돌려줍니다.

목적은 `ADOPT`이면 `isAdoptable()`, `FOSTER`이면 `isFosterable()` 값을 읽습니다. 테스트에서 `FOSTER`는 Milo와 Poppy입니다. 두 값은 독립적이므로 한 동물이 둘 다 가능할 수 있습니다. 실제 가능 여부는 아닌 학습용 값입니다.

제공된 `ExerciseHelpers.normalizeText(String text) -> String`은 null이 아닌 문자열을 받아 앞뒤 공백을 지우고 영문 대소문자를 통일합니다. 예: `ExerciseHelpers.normalizeText("  MIX  ")`는 `"mix"`, 공백만 있는 문자열은 `""`입니다. 원본 문자열을 바꾸지 않습니다. 이 도우미는 비교나 결과 목록 생성을 대신하지 않습니다.

힌트 1: 검색어 정리와 동물 한 마리의 조건 검사를 나누어 생각하세요. 힌트 2: “이름 또는 품종”에는 두 곳 중 한 곳만 맞아도 됩니다. 힌트 3: 목적 문자열 자체와 동물의 참/거짓 값은 자료형이 다릅니다. 결과를 새 목록에 담는 점은 앞 수업과 같습니다.

흔한 실수: 이름만 검색하기, 정확히 같은 단어만 찾기, 입양 가능의 반대를 임시 보호 가능으로 취급하기. 이미 한 필터를 거친 입력도 올 수 있으므로 전체 Catalog를 다시 읽지 마세요.

```sh
backend/mvnw -f backend/pom.xml -Pexercises '-Dtest=AnimalExercisesTest$Lesson4' test
```

통과 기준은 검색어 정리·부분 검색과 독립적인 목적 조건입니다. Java 재시작 후 화면에서 검색어 또는 목적 하나씩, 그다음 종류·나이와 함께 확인하세요. 여러 필터는 모두 만족해야 하며 종류 → 나이 → 검색어 → 목적 순서로 적용됩니다.

## 이제 HTTP와 JSON 살펴보기

앞의 메서드는 Java 값을 받아 Java 값을 반환합니다. 화면은 HTTP 요청으로 그 메서드를 사용합니다. 예를 들어 브라우저에서 [종류 요청](http://127.0.0.1:8080/api/animals?species=DOG)을 열면 Java가 처리하고 JSON으로 `items` 목록과 `total` 개수를 보냅니다. Python은 그 결과를 받아 표시합니다. 검색 규칙을 Python에 다시 구현하지 마세요.

미완성 메서드는 501과 `lessonId`, 잘못된 입력은 400, 조회한 ID가 없으면 404입니다. 오류는 “결과 0개”와 다릅니다. 전체 규칙은 [API 문서](api.md)에 있습니다.

## 수업 6 — 중복 없는 보호소 수 (`lesson-6`)

수정 위치: `AnimalExercises.countDistinctShelters(ArrayList<Animal> animals) -> int`.

입력은 현재 필터 결과 목록입니다. 각 동물의 `getShelterId()`를 읽어 서로 다른 보호소 개수를 반환합니다. 예: `sfacc, phs, sfacc`는 `2`, 빈 목록은 `0`입니다. 원본과 순서를 바꾸지 않고 매번 새 `HashSet<String>`을 만드세요. 필요한 import를 학생이 추가합니다.

힌트: 목록의 길이와 중복을 제거한 크기는 다릅니다. 같은 ID를 두 번 넣으면 `HashSet`의 크기가 어떻게 될까요? 전역 Catalog를 다시 읽으면 현재 검색 결과를 세지 않게 됩니다. 메서드의 Step 1–4를 따라 완성한 뒤 마지막 예외를 제거하고 값을 반환하세요.

```sh
backend/mvnw -f backend/pom.xml -Pexercises '-Dtest=AnimalExercisesTest$Lesson6' test
```

Java를 재시작하면 전체 목록 또는 구현된 필터 결과에 `Shelters`가 표시됩니다. 수업 7이 미완성이어도 이 숫자는 바로 보입니다.

## 수업 7 — 종류별 개수와 배열 초기화 (`lesson-7`)

수정 위치: `AnimalExercises.countBySpecies(ArrayList<Animal> animals) -> HashMap<String, Integer>`.

입력은 현재 결과 목록이며 출력은 `DOG`와 `CAT` 키를 모두 가진 새 맵입니다. 예: `DOG, CAT, DOG`는 `DOG=2, CAT=1`이고, DOG만 있으면 CAT도 `0`으로 포함해야 합니다. 빈 목록은 두 값 모두 `0`입니다. 입력 목록·순서를 바꾸지 마세요.

제공된 `ExerciseHelpers.supportedSpecies() -> String[]`는 인자 없이 매번 새 배열을 반환하며 순서는 `DOG`, `CAT`입니다. 예: 반환한 배열의 길이는 2, 첫 값은 `DOG`입니다. 한 호출의 배열을 바꿔도 다음 호출의 배열은 바뀌지 않습니다. 이 도우미는 맵을 만들거나 개수를 세지 않습니다.

먼저 별도 연습장에서 `String[]`을 받아 `for`로 두 값을 순서대로 읽어 보세요. 그다음 결과 맵의 두 키를 0으로 준비하는 단계와 동물 목록에서 개수를 누적하는 단계를 구분하세요. 아직 없는 키를 읽은 결과에 숫자를 더하려 하면 어떤 문제가 생길까요? 메서드의 Step 1–4를 따라 완성하고 마지막 예외를 제거하세요.

```sh
backend/mvnw -f backend/pom.xml -Pexercises '-Dtest=AnimalExercisesTest$Lesson7' test
```

Java 재시작 후 `DOG`, `CAT` 숫자가 보입니다. 수업 6과 7은 독립적이며 `summary`는 같은 응답의 `items`를 설명합니다. `null`/수업 안내는 미완성, `0`은 계산된 실제 영입니다. 요약을 보려고 Python 과제나 선택 ID 조회를 먼저 풀 필요가 없습니다.

## 수업 5 — Python 기회 문구 목록 (`lesson-5`)

수정 위치: `frontend/student_ui.py`의 `opportunity_labels(animal: dict) -> list[str]`. 입력은 동물 한 마리의 딕셔너리이고 출력은 새 문자열 목록입니다. NiceGUI를 import하거나 화면을 직접 만들지 않습니다. `animal['adoptable']`, `animal['fosterable']`는 서로 독립적인 Python boolean입니다.

- 참/거짓 → `['Mock adoption option']`
- 거짓/참 → `['Mock foster option']`
- 참/참 → `['Mock adoption option', 'Mock foster option']` (이 순서)
- 거짓/거짓 → `[]`

함수 안의 Step 1–4는 새 목록 준비 → 두 값 읽기 → 각각 조건을 검사해 문자열 추가 → 목록 반환입니다. 완성하면 마지막 `raise ExerciseNotImplementedError('lesson-5')`를 제거하세요. `if/elif`로 두 목적을 서로 배타적으로 다루지 않는지 생각하세요. 문자열 `"False"`와 boolean `False`는 다릅니다. 입력 딕셔너리는 수정하지 않습니다.

제공된 `frontend/ui_helpers.py`의 API:

- `get_opportunity_labels(animal: dict) -> list[str] | None`: 학생 함수 결과를 받아옵니다. 전용 미완성 예외일 때만 `None`이며, 잘못된 반환형이나 일반 버그는 드러납니다. 완성된 거짓/거짓 결과 `[]`와 미완성 `None`은 다릅니다.
- `render_opportunity_badges(labels: list[str]) -> None`: 예를 들어 `render_opportunity_badges(['Mock adoption option'])`은 받은 문자열을 표시합니다. 어떤 문구를 넣을지는 대신 결정하지 않습니다.
- `render_summary(summary: dict) -> None`: 예를 들어 `{'shelterCount': 0, 'speciesCounts': {'DOG': 0, 'CAT': 0}, 'unfinishedLessons': []}`를 받으면 세 실제 0을 표시합니다. 일부 요약이 `None`이면 사용 가능한 숫자와 미완성 수업 안내를 함께 표시합니다.

이 도우미와 앱 연결 코드는 수정할 과제가 아닙니다. 카탈로그와 선택 상세 화면 모두 학생이 반환한 문구를 표시하며, 미완성 안내는 페이지에 한 번만 표시됩니다. 실제 신청이나 현재 가능 여부는 공식 보호소에 확인합니다.

서버·Java·브라우저 없이 네 로컬 딕셔너리 조합을 자동 검사합니다:

```sh
.venv/bin/python -m pytest frontend/exercises -q
```

초기 실패는 의도된 `ExerciseNotImplementedError: lesson-5`입니다. 이 검사는 기본 `frontend/tests` 밖에 있으므로 연결 검사와 구별됩니다. 테스트를 고치거나 fixture에 정답을 넣지 마세요. Python 변경 후 서버를 재시작하면 카탈로그에서 확인할 수 있습니다. 수업 3은 필요 없습니다. 선택 상세 화면만 ID 조회 구현이 필요합니다.

## 명령을 읽는 법

위 Java 명령의 `-Pexercises`는 학생 과제를 켜고 `$LessonN`은 이번 수업만 선택합니다. Windows PowerShell에서는 앞부분만 `.\backend\mvnw.cmd -f backend\pom.xml`로 바꾸고, 작은따옴표와 수업 이름은 그대로 사용하세요. Python 기본 검사는 `.\.venv\Scripts\python.exe -m pytest frontend\tests`, opt-in 수업 5는 `.\.venv\Scripts\python.exe -m pytest frontend\exercises -q`입니다.

기본 `backend/mvnw -f backend/pom.xml test`가 통과해도 학생 과제는 미완성일 수 있습니다. 테스트 코드를 고쳐 통과시키지 말고 실패 메시지의 입력·기댓값과 본인 메서드를 비교하세요. 하나를 고친 후 해당 수업만 다시 확인하고, 마무리할 때 기본 Java/Python 검사도 실행합니다.


기본 검사(macOS/Linux):

```sh
backend/mvnw -f backend/pom.xml test
.venv/bin/python -m pytest frontend/tests -q
```

Windows PowerShell:

```powershell
.\backend\mvnw.cmd -f backend\pom.xml test
.\.venv\Scripts\python.exe -m pytest frontend\tests -q
```

고정된 통과 개수 대신 명령의 실제 출력과 실패한 수업 번호를 확인하세요. 기본 검사는 학생 알고리즘을 대신 구현하지 않고 연결과 렌더링을 검사합니다. Java를 바꾸면 Java 서버, Python을 바꾸면 Python 서버를 Ctrl-C로 종료 후 원래 명령으로 재시작하고 브라우저를 새로 고칩니다.

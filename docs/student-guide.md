# 학생 가이드

먼저 [README 실행 순서](../README.md)를 따라 전체 목록을 보세요. 모든 보호소의 가상 동물을 모아서 보여주고, 동물을 고르면 실제 보호소 사이트로 연결됩니다. 실제 동물이나 현재 입양 가능 여부를 표시하지 않습니다.

수업 1~4에서 수정할 파일은 [AnimalExercises.java](../backend/src/main/java/org/royshelter/AnimalExercises.java) 하나입니다. 수업 5는 [student_ui.py](../frontend/student_ui.py) 하나를 수정합니다. 서버 연결, URL 처리, 화면 전체 구조는 이미 준비되어 있습니다. `for`, `if`, `ArrayList`, `HashMap`을 사용해 충분히 풀 수 있습니다. Stream, 람다, 디자인 패턴은 필요하지 않습니다. `HashSet`은 이미 배웠지만 이 제품에는 보호소 선택 필터가 없으므로 필수 과제로 사용하지 않습니다.

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

통과 기준은 2개 테스트입니다. 아직 구현하지 않았다면 `lesson-1` 예외로 실패합니다.

## 수업 2 — 최대 나이 (`lesson-2`)

수정 위치: `AnimalExercises.filterByMaxAge(ArrayList<Animal> animals, int maxAgeMonths)`.

입력은 목록과 0 이상의 최대 개월 수입니다. 출력은 최대 나이 **이하**인 동물의 새 목록이며 순서를 유지합니다. 테스트 목록에서 최대 24개월이면 0개월 Luna와 24개월 Milo가 남고, 25개월 Poppy는 빠집니다. 최대 0개월도 유효합니다.

힌트 1: 사람 나이가 아닌 `ageMonths`를 읽습니다. 힌트 2: “이하”는 경계값 자체를 포함합니다. 힌트 3: 결과가 0마리인 경우도 정상적인 목록입니다.

흔한 실수: `<`만 사용해 정확히 24개월인 동물을 빼거나 0개월을 잘못된 값으로 취급하기. 이 메서드는 전달된 목록을 읽고 새 목록을 돌려줍니다.

```sh
backend/mvnw -f backend/pom.xml -Pexercises '-Dtest=AnimalExercisesTest$Lesson2' test
```

통과 기준은 경계값과 0개월을 확인하는 2개 테스트입니다.

## 수업 3 — ID로 한 마리 찾기 (`lesson-3`, 선택)

수정 위치: `AnimalExercises.findById(ArrayList<Animal> animals, String id)`.

입력은 동물 목록과 찾을 ID, 출력은 그 동물 하나 또는 없을 때 `null`입니다. `HashMap<String, Animal>`에서 ID를 키로 사용해 보세요. 테스트의 ID `b`는 Milo를 반환하고 `missing`은 `null`을 반환합니다. 새 동물을 만들어 반환하는 과제가 아닙니다.

힌트 1: 지도에서 주소로 장소를 찾듯, ID와 객체를 연결합니다. 힌트 2: `HashMap`의 키와 값이 각각 무엇인지 말해 보세요. 힌트 3: `get`은 없는 키에 대해 `null`을 돌려줄 수 있습니다. `null`에 `getName()`을 호출하면 어떻게 될지 생각해 보세요.

흔한 실수: 이름을 키로 저장하고 ID로 조회하기, 없는 ID를 “첫 번째 동물”로 대신 반환하기. 컨트롤러가 `null`을 404로 바꿔 주므로 여기서는 HTTP 코드를 만들 필요가 없습니다.

```sh
backend/mvnw -f backend/pom.xml -Pexercises '-Dtest=AnimalExercisesTest$Lesson3' test
```

통과 기준은 존재/부재 2개 테스트입니다. Java 재시작 후 [별도 연습 페이지](http://127.0.0.1:8081/animals/mock-sfacc-01)에서도 확인할 수 있습니다. 기본 목록의 동물 이름은 공식 보호소로 바로 연결되므로 이 수업을 풀지 않아도 주 탐색 경로가 동작합니다.

## 수업 4 — 검색어와 연결 목적 (`lesson-4`)

수정 위치: `AnimalExercises.filterByQuery(ArrayList<Animal> animals, String query)` 및 `filterByPurpose(ArrayList<Animal> animals, String purpose)`.

검색어는 앞뒤 공백과 영문 대소문자를 무시하고 **이름 또는 품종의 부분 문자열**과 비교합니다. 출력은 일치하는 동물의 새 목록이며 원래 순서를 유지합니다. 테스트 입력 `"  LuN  "`은 Luna, `"MIX"`는 Luna와 Poppy입니다. 공백뿐인 검색어는 동물 모두를 같은 순서로 돌려줍니다.

목적은 `ADOPT`이면 `isAdoptable()`, `FOSTER`이면 `isFosterable()` 값을 읽습니다. 테스트에서 `FOSTER`는 Milo와 Poppy입니다. 두 값은 독립적이므로 한 동물이 둘 다 가능할 수 있습니다. 실제 가능 여부는 아닌 학습용 값입니다.

힌트 1: 검색어 정리와 동물 한 마리의 조건 검사를 나누어 생각하세요. 힌트 2: “이름 또는 품종”에는 두 곳 중 한 곳만 맞아도 됩니다. 힌트 3: 목적 문자열 자체와 동물의 참/거짓 값은 자료형이 다릅니다. 결과를 새 목록에 담는 점은 앞 수업과 같습니다.

흔한 실수: 이름만 검색하기, 정확히 같은 단어만 찾기, 입양 가능의 반대를 임시 보호 가능으로 취급하기. 이미 한 필터를 거친 입력도 올 수 있으므로 전체 Catalog를 다시 읽지 마세요.

```sh
backend/mvnw -f backend/pom.xml -Pexercises '-Dtest=AnimalExercisesTest$Lesson4' test
```

통과 기준은 검색어 3개, 목적 2개의 총 5개 테스트입니다. Java 재시작 후 화면에서 검색어 또는 목적 하나씩, 그다음 종류·나이와 함께 확인하세요. 여러 필터는 모두 만족해야 하며 종류 → 나이 → 검색어 → 목적 순서로 적용됩니다.

## 이제 HTTP와 JSON 살펴보기

앞의 메서드는 Java 값을 받아 Java 값을 반환합니다. 화면은 HTTP 요청으로 그 메서드를 사용합니다. 예를 들어 브라우저에서 [종류 요청](http://127.0.0.1:8080/api/animals?species=DOG)을 열면 Java가 처리하고 JSON으로 `items` 목록과 `total` 개수를 보냅니다. Python은 그 결과를 받아 표시합니다. 검색 규칙을 Python에 다시 구현하지 마세요.

미완성 메서드는 501과 `lessonId`, 잘못된 입력은 400, 조회한 ID가 없으면 404입니다. 오류는 “결과 0개”와 다릅니다. 전체 규칙은 [API 문서](api.md)에 있습니다.

## 수업 5 — Python 안내 문구 (`lesson-5`, 선택)

수정 위치: `frontend/student_ui.py`의 `render_opportunities(animal: dict) -> None`. 입력은 Java에서 받은 동물 한 마리의 딕셔너리이며 반환값 대신 화면에 문구를 표시합니다. `animal`의 키 `adoptable`, `fosterable`은 Python의 참/거짓 값입니다. `if`와 NiceGUI의 `ui.label`을 사용해 각 목적에 대한 안내를 만들어 보세요. 두 값이 모두 참이면 두 안내가 모두 보여야 합니다. 모두 거짓이면 가능하다는 안내를 만들지 않습니다.

예: `adoptable`이 참이고 `fosterable`이 거짓인 가상 동물은 입양 연습 안내만 표시합니다. 이와 별개로 “가상 동물이며 실제 신청은 보호소에서 한다”는 안내를 유지하세요. 개별 동물을 실제 신청할 수 있다는 문구는 쓰지 마세요.

힌트 1: 딕셔너리에서는 키로 값을 읽습니다. 힌트 2: 문자열 `"False"`와 참/거짓 `False`는 다릅니다. 힌트 3: 두 값이 독립적일 때 `if`와 `elif`의 차이를 생각하세요. 기본 안내를 지워 버리거나 검색 규칙을 추가하는 것이 흔한 실수입니다.

검증 명령은 연결 코드가 깨지지 않았는지 확인하는 기본 검사입니다. 안내의 정답을 자동 판정하지 않습니다.

```sh
.venv/bin/python -m pytest frontend/tests
```

수업 3을 구현한 뒤 두 서버를 실행하고 Python 변경 후 재시작하여 위 별도 페이지를 확인하세요. 현재 fixture에서 `mock-sfacc-01`은 참/거짓, `mock-sfacc-02`는 거짓/참, `mock-sfacc-03`은 참/참 예입니다. 모두 거짓인 경우는 교사와 임시 딕셔너리로 별도 확인하고 fixture를 정답용으로 바꾸지 마세요.

## 명령을 읽는 법

위 Java 명령의 `-Pexercises`는 학생 과제를 켜고 `$LessonN`은 이번 수업만 선택합니다. Windows PowerShell에서는 앞부분만 `.\backend\mvnw.cmd -f backend\pom.xml`로 바꾸고, 작은따옴표와 수업 이름은 그대로 사용하세요. Python은 `.\.venv\Scripts\python.exe -m pytest frontend\tests`입니다.

기본 `backend/mvnw -f backend/pom.xml test`가 통과해도 학생 과제는 미완성일 수 있습니다. 테스트 코드를 고쳐 통과시키지 말고 실패 메시지의 입력·기댓값과 본인 메서드를 비교하세요. 하나를 고친 후 해당 수업만 다시 확인하고, 마무리할 때 기본 Java/Python 검사도 실행합니다.

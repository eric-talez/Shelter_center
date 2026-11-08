# 교사용 가이드

목표는 “배운 반복문과 컬렉션으로 실제 화면의 작은 규칙을 만든다”입니다. 학습자의 설명과 경계값 이해를 기준으로 다음 수업으로 이동하며 고정 수업 시간은 정하지 않습니다. 정답 구현은 이 저장소에 없습니다.

## 수업 전 준비

[README](../README.md)의 두 서버와 기본 검사 명령을 교사 컴퓨터에서 먼저 실행하세요. JDK 21, Python 3.13과 프로젝트 `.venv`를 확인합니다. 학생에게는 Java 문법, 변수, 메서드 호출, `for`, `if`, `ArrayList`의 생성/추가, `HashSet`의 중복 제거, `HashMap`의 키/값, 배열 순회를 짧게 확인합니다. `HashSet`은 수업 6의 중복 없는 보호소 수에 사용합니다. 수업 7은 `HashMap`과 `String[]`을 통한 키 초기화를 연습합니다. 권장 순서는 준비 → 1 → 2 → 4 → 6 → 7 → 5이며 수업 3은 선택입니다.

전체 20마리가 먼저 표시되고 동물 이름이 공식 보호소 사이트를 여는지 확인합니다. 가상 동물과 실제 보호소의 구분, 비제휴 안내, 실제 신청은 보호소에서 한다는 경계를 학생이 설명하도록 합니다. HTTP/JSON 설명은 처음 Java 규칙 연습 후로 미룹니다.

## 첫 수업 함께 진행하기

1. [Animal](../backend/src/main/java/org/royshelter/Animal.java)의 이름·종류 getter를 함께 찾습니다. [학생 가이드 준비 연습](student-guide.md)의 공급된 세 동물을 읽고 이름을 순서대로 말하도록 합니다.
2. [AnimalExercises](../backend/src/main/java/org/royshelter/AnimalExercises.java)의 `filterBySpecies` 메서드 서명에서 입력과 출력을 말하게 합니다. 원본 목록과 새 결과 목록을 종이에 따로 그립니다.
3. 수업 1 테스트를 실행해 아직 예외로 실패하는 모습을 보여줍니다. 정답을 입력해 주기 전에 “세 동물을 하나씩 보면 무엇을 비교할까?”를 묻습니다.
4. 학생이 한 번 구현하고 다시 같은 테스트를 실행하게 합니다. `.equals`와 `==`, 입력 목록을 보존하는 이유를 설명하게 합니다.
5. 통과 후 Java 서버를 재시작하고 화면의 Species 검색과 Reset을 확인합니다. 코드 저장만으로 실행 중 Java가 바뀌지는 않습니다.

테스트의 프레임워크나 컨트롤러 구조를 처음부터 가르칠 필요는 없습니다. 학생이 수정하는 Java 파일은 하나로 유지하고 연결 코드는 교사가 관리합니다.

## 목표·관찰 질문·검증

아래 명령은 프로젝트 최상위에서 JDK 21을 선택한 후 실행합니다. Windows PowerShell은 `backend/mvnw -f backend/pom.xml`을 `.\backend\mvnw.cmd -f backend\pom.xml`로 바꾸며 인용된 `$LessonN`은 유지합니다.

| 수업 | 목표와 선행 지식 | 관찰 질문 | 검사 |
| --- | --- | --- | --- |
| 준비 | 객체 getter, 목록과 요소 구분, 반복문 | 한 마리와 목록의 자료형은? Luna → Milo → Poppy 순서가 어디서 오는가? | 디버거/연습장에서 이름을 읽고 순서를 설명 |
| 1 | `for`/`if`/`ArrayList`, 문자열 내용 비교 | `==`로 같은 글자를 비교하면 왜 위험할까? 원본에서 삭제하면 무슨 일이 생길까? | `-Pexercises '-Dtest=AnimalExercisesTest$Lesson1' test` |
| 2 | 숫자 비교, 경계값 | 24개월 이하에 24개월이 포함되는가? 0개월은? | `-Pexercises '-Dtest=AnimalExercisesTest$Lesson2' test` |
| 3, 선택 | `HashMap<String, Animal>`, `null` | 키를 이름으로 하면 ID 검색이 될까? 없는 키의 `get` 결과를 어디서 처리하는가? | `-Pexercises '-Dtest=AnimalExercisesTest$Lesson3' test` |
| 4 | 문자열 정리·부분 검색, 독립 boolean | 이름/품종 중 하나만 맞으면? 입양과 임시 보호가 둘 다 참이면? | `-Pexercises '-Dtest=AnimalExercisesTest$Lesson4' test` |
| 6 | `HashSet<String>`, 지역 컬렉션 | 같은 보호소가 세 번 나오면 몇 곳인가? 빈 결과는? | `-Pexercises '-Dtest=AnimalExercisesTest$Lesson6' test` |
| 7 | `HashMap<String, Integer>`, `String[]` 초기화 | CAT가 없을 때 키를 생략해도 될까? 없는 키를 읽으면? | `-Pexercises '-Dtest=AnimalExercisesTest$Lesson7' test` |
| 5 | Python 딕셔너리, 독립 조건문, 문자열 목록 | `if/elif`이면 두 문구가 모두 나올까? `[]`와 `None`은? | `.venv/bin/python -m pytest frontend/exercises -q` |

예를 들어 수업 4의 전체 명령:

```sh
backend/mvnw -f backend/pom.xml -Pexercises '-Dtest=AnimalExercisesTest$Lesson4' test
```

수업 3의 ID 조회 API와 `/animals/{id}` 화면은 선택 학습용입니다. 기본 탐색은 목록에서 공식 보호소로 바로 연결되므로 조회 구현을 강제하지 않습니다. 수업 5는 로컬 딕셔너리 자동 검사와 카탈로그 표시만으로 완료할 수 있으며 수업 3이 필요하지 않습니다. 선택 상세 화면에서만 ID 조회 구현이 필요합니다.

힌트는 [학생 가이드](student-guide.md)의 개념 → 비교 대상 → 경계값 순으로 제공합니다. 반복문 전체를 대신 작성하거나 테스트를 약화시키지 않습니다. 테스트가 통과해도 소스 목록 변경, 전체 Catalog 재조회, 두 목적을 서로 반대로 취급하는지 코드를 보고 질문하세요.

## 기본 검사와 학생 과제는 다릅니다

```sh
backend/mvnw -f backend/pom.xml test
backend/mvnw -f backend/pom.xml package
.venv/bin/python -m pytest frontend/tests
```

Windows PowerShell:

```powershell
.\backend\mvnw.cmd -f backend\pom.xml test
.\backend\mvnw.cmd -f backend\pom.xml package
.\.venv\Scripts\python.exe -m pytest frontend\tests
```

기본 검사는 학생 정답 없이 통과해야 합니다. 고정된 테스트 총개수 대신 각 명령의 실제 결과와 실패 원인을 확인하세요. Java 기본 검사는 `exercise` 태그를 제외하고 Catalog의 fixture 일관성, Controller의 입력·오류 처리, 독립적인 요약 연결, 도우미 동작을 확인합니다. Controller는 작은 test double을 사용합니다. Python 기본 검사는 HTTP 검증, 비동기 화면 상태, 제공된 표시 도우미를 검사하며 학생 함수가 계속 미완성이어야 한다고 가정하지 않습니다.

모든 Java 학생 과제를 실행하려면:

```sh
backend/mvnw -f backend/pom.xml -Pexercises test
```

starter의 학생 과제는 해당 수업의 `ExerciseNotImplementedException`으로 실패해야 정상입니다. 이를 기본 검사가 실패한 것으로 보고 해결하려고 정답을 선구현하지 마세요. 수업 4만 선택하면 `lesson-4`로 실패합니다. UI의 501 안내는 예상 학습 상태이고, 400은 입력 문제, 500은 예상 밖 버그, 연결 불가는 별도 장애입니다.

Python 수업 5의 opt-in 검사(macOS/Linux / Windows PowerShell):

```sh
.venv/bin/python -m pytest frontend/exercises -q
```

```powershell
.\.venv\Scripts\python.exe -m pytest frontend\exercises -q
```

네 로컬 boolean 조합은 Java·HTTP·브라우저 없이 검사되며 초기에는 모두 `ExerciseNotImplementedError: lesson-5`로 실패합니다. 학생이 수정할 파일은 `AnimalExercises.java`(일곱 Java 메서드)와 `frontend/student_ui.py`의 순수 `opportunity_labels(animal: dict) -> list[str]`뿐입니다. 각 메서드의 English Step 1–4와 입력·출력 예시를 읽고 완성한 뒤 마지막 예외를 제거하게 하세요. 입력 보존과 호출마다 새 컬렉션을 사용하는지도 확인합니다.

제공된 도우미는 정답 알고리즘이 아닙니다. `ExerciseHelpers.normalizeText(String text) -> String`은 `"  MIX  "`를 `"mix"`로 정리하고, `ExerciseHelpers.supportedSpecies() -> String[]`는 매번 새 `DOG`, `CAT` 배열을 줍니다. 수업 7 전에 배열을 순회하여 맵의 0 키를 준비하는 연습을 별도로 시키세요. Python `get_opportunity_labels(animal: dict) -> list[str] | None`은 학생 결과를 받으며 전용 미완성 예외만 `None`으로 바꿉니다. `render_opportunity_badges(labels: list[str]) -> None`은 전달한 문자열만 표시합니다. `render_summary(summary: dict) -> None`은 사용 가능한 숫자를 표시합니다. 정확한 예시와 네 문구 조합은 [학생 가이드](student-guide.md)에 있습니다. 일반 버그를 미완성으로 숨기거나 정답을 도우미에 넣지 마세요.

수업 6·7은 현재 필터 결과의 요약이며 서로 독립적입니다. Java 재시작 후 한 수업만 완성해도 그 숫자는 보이고 다른 수업 안내만 남습니다. 실제 `0`과 미완성 `null`을 구분하게 하세요. 수업 5가 미완성이어도 요약은 보이며, Python 변경 후 Python 서버도 재시작합니다. 카탈로그의 lesson-5 안내는 한 번만 보이고 완성 시 각 카드와 선택 상세 화면에 반환된 문구가 나타납니다. 공식 보호소 링크와 가상 데이터 안내는 유지합니다.

## 데이터와 후속 확장

[동물 fixture](../backend/src/main/resources/data/animals.json), [보호소 fixture](../backend/src/main/resources/data/shelters.json), [출처 기록](data-sources.md)을 함께 봅니다. 이름·연락처·공식 URL은 실제 기관 자료, 동물과 상태는 모두 가상입니다. `demo: true`와 화면 안내를 유지하세요. 실제 신청·개별 실제 동물로 연결된다고 말하지 않습니다.

새 연습 데이터는 같은 JSON 필드 구조와 고유 ID, 기존 shelterId 연결을 지켜 파일 내용을 바꿀 수 있습니다. Catalog는 시작할 때 이를 검증하므로 변경 후 테스트와 Java 재시작이 필요합니다. 동물 개수를 바꾸면 기본 검사의 고정 개수 기대도 새 학습 데이터에 맞게 검토합니다. 현재 5개 기관의 공식 HTTPS 호스트만 허용하므로 기관 추가는 교사가 Catalog의 허용 목록과 출처를 함께 검토해야 합니다. 지금은 크롤러·외부 API 어댑터를 만들지 않습니다. 실제 데이터 도입은 출처·사용 조건·가상 안내 정책을 별도 검토하는 후속 작업입니다.

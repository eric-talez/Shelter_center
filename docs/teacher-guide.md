# 교사용 가이드

목표는 “배운 반복문과 컬렉션으로 실제 화면의 작은 규칙을 만든다”입니다. 학습자의 설명과 경계값 이해를 기준으로 다음 수업으로 이동하며 고정 수업 시간은 정하지 않습니다. 정답 구현은 이 저장소에 없습니다.

## 수업 전 준비

[README](../README.md)의 두 서버와 기본 검사 명령을 교사 컴퓨터에서 먼저 실행하세요. JDK 21, Python 3.13과 프로젝트 `.venv`를 확인합니다. 학생에게는 Java 문법, 변수, 메서드 호출, `for`, `if`, `ArrayList`의 생성/추가, `HashMap`의 키/값을 짧게 확인합니다. `HashSet`은 복습할 수 있지만 보호소 선택 필터는 없으므로 제품 과제에 억지로 넣지 않습니다.

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
| 1 | `for`/`if`/`ArrayList`, 문자열 내용 비교 | `==`로 같은 글자를 비교하면 왜 위험할까? 원본에서 삭제하면 무슨 일이 생길까? | `-Pexercises '-Dtest=AnimalExercisesTest$Lesson1' test` (2개) |
| 2 | 숫자 비교, 경계값 | 24개월 이하에 24개월이 포함되는가? 0개월은? | `-Pexercises '-Dtest=AnimalExercisesTest$Lesson2' test` (2개) |
| 3, 선택 | `HashMap<String, Animal>`, `null` | 키를 이름으로 하면 ID 검색이 될까? 없는 키의 `get` 결과를 어디서 처리하는가? | `-Pexercises '-Dtest=AnimalExercisesTest$Lesson3' test` (2개) |
| 4 | 문자열 정리·부분 검색, 독립 boolean | 이름/품종 중 하나만 맞으면? 입양과 임시 보호가 둘 다 참이면? | `-Pexercises '-Dtest=AnimalExercisesTest$Lesson4' test` (5개) |
| 5, 선택 | Python 딕셔너리 읽기, 조건문, `ui.label` | `if/elif`이면 두 안내가 모두 나올까? `False`와 `"False"`는 같은가? | Python 기본 검사 + 네 가지 참/거짓 조합 수동 확인 |

예를 들어 수업 4의 전체 명령:

```sh
backend/mvnw -f backend/pom.xml -Pexercises '-Dtest=AnimalExercisesTest$Lesson4' test
```

수업 3의 ID 조회 API와 `/animals/{id}` 화면은 선택 학습용입니다. 기본 탐색은 목록에서 공식 보호소로 바로 연결되므로 조회 구현을 강제하지 않습니다. 수업 5의 실제 상세 화면 확인에는 수업 3 구현이 필요합니다. 먼저 Python 딕셔너리로 연습해도 됩니다.

힌트는 [학생 가이드](student-guide.md)의 개념 → 비교 대상 → 경계값 순으로 제공합니다. 반복문 전체를 대신 작성하거나 테스트를 약화시키지 않습니다. 테스트가 통과해도 소스 목록 변경, 전체 Catalog 재조회, 두 목적을 서로 반대로 취급하는지 코드를 보고 질문하세요.

## 기본 검사와 학생 과제는 다릅니다

```sh
backend/mvnw -f backend/pom.xml test
backend/mvnw -f backend/pom.xml package
.venv/bin/python -m pytest frontend/tests
```

Windows Python 검사는 `.\.venv\Scripts\python.exe -m pytest frontend\tests`입니다. 기본 Java 검사는 Catalog 3개와 Controller 14개(총 17개), Python 기본 검사는 22개입니다. 이 숫자는 현재 starter 기준이며 이후 의미 있는 검사를 추가하면 바뀔 수 있습니다. 기본 Java 검사는 `exercise` 태그를 제외하며, 입력 검증·에러 매핑·목록 반환·fixture 일관성을 확인합니다. Controller 검사는 고정 결과/예외를 주는 작은 test double로 연결을 검사하므로 **학생 정답이 없어도 통과**합니다. Python 검사는 HTTP 응답 처리, 타임아웃, URL 인코딩, 비동기 화면 상태를 검사합니다. 외부 보호소 사이트의 상태나 Python 문구 정답을 검사하지 않습니다.

모든 Java 학생 과제를 실행하려면:

```sh
backend/mvnw -f backend/pom.xml -Pexercises test
```

starter는 11개 과제가 `ExerciseNotImplementedException`으로 실패해야 정상입니다. 이를 기본 검사가 실패한 것으로 보고 해결하려고 정답을 선구현하지 마세요. 수업 4만 실행하면 5개가 `lesson-4`로 실패합니다. UI의 501 안내는 예상 학습 상태이고, 400은 입력 문제, 500은 예상 밖 버그, 연결 불가는 별도 장애입니다.

## 데이터와 후속 확장

[동물 fixture](../backend/src/main/resources/data/animals.json), [보호소 fixture](../backend/src/main/resources/data/shelters.json), [출처 기록](data-sources.md)을 함께 봅니다. 이름·연락처·공식 URL은 실제 기관 자료, 동물과 상태는 모두 가상입니다. `demo: true`와 화면 안내를 유지하세요. 실제 신청·개별 실제 동물로 연결된다고 말하지 않습니다.

새 연습 데이터는 같은 JSON 필드 구조와 고유 ID, 기존 shelterId 연결을 지켜 파일 내용을 바꿀 수 있습니다. Catalog는 시작할 때 이를 검증하므로 변경 후 테스트와 Java 재시작이 필요합니다. 동물 개수를 바꾸면 기본 검사의 고정 개수 기대도 새 학습 데이터에 맞게 검토합니다. 현재 5개 기관의 공식 HTTPS 호스트만 허용하므로 기관 추가는 교사가 Catalog의 허용 목록과 출처를 함께 검토해야 합니다. 지금은 크롤러·외부 API 어댑터를 만들지 않습니다. 실제 데이터 도입은 출처·사용 조건·가상 안내 정책을 별도 검토하는 후속 작업입니다.

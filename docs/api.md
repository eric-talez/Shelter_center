# 로컬 API 규약

기본 주소는 `http://127.0.0.1:8080`입니다. [AnimalController](../backend/src/main/java/org/royshelter/AnimalController.java)가 요청을 검증하고 [AnimalExercises](../backend/src/main/java/org/royshelter/AnimalExercises.java)에 검색·조회 규칙을 맡깁니다. Python은 검색을 대신 구현하지 않습니다. 인증·페이지 나누기는 없습니다. 현재 동물은 모두 가상이며 실제 보호소와 제휴하지 않습니다.

## GET /api/animals

조건이 없으면 모든 보호소의 동물 20마리를 fixture 순서로 반환합니다. 지원 쿼리는 아래 네 개뿐입니다. `shelterId`를 포함한 다른 키는 값이 비어 있어도 400입니다. 보호소를 선택하는 단계는 없습니다. 지원 키도 같은 키를 두 번 쓰면 빈 값이어도 400입니다.

| 키 | 생략/빈 값 | 유효한 값과 처리 | 학생 메서드 |
| --- | --- | --- | --- |
| `species` | 필터 생략 | 정확히 `DOG` 또는 `CAT`; 대소문자 구별, 공백 제거 안 함 | `filterBySpecies`, lesson-1 |
| `maxAgeMonths` | 필터 생략 | ASCII 숫자만으로 된 0~2147483647의 정수. `0`, `024` 허용; 음수, `+1`, 소수, 공백, 범위 초과 거부. 나이 이하(경계 포함) | `filterByMaxAge`, lesson-2 |
| `q` | 앞뒤 공백 제거 후 비면 필터 생략 | 길이 제한 없음. 이름 또는 품종 부분 검색, 영문 대소문자 무시. 컨트롤러가 `String.trim()` 후 전달 | `filterByQuery`, lesson-4 |
| `purpose` | 필터 생략 | 정확히 `ADOPT` 또는 `FOSTER`; 대소문자 구별, 공백 제거 안 함. 각각 독립적인 adoptable/fosterable 값 사용 | `filterByPurpose`, lesson-4 |

모든 파라미터를 검증한 뒤 종류 → 최대 나이 → 검색어 → 목적 순서로 적용합니다. 각 단계는 앞 단계의 결과를 입력으로 받으므로 조건들은 AND입니다. 조건을 만족하는 목록 순서를 유지합니다. 특정 보호소 조건은 없습니다. 처음 starter에서는 비어 있지 않은 유효 필터 요청이 해당 TODO에서 501을 반환합니다. 여러 미완성 조건이면 순서상 처음 만난 lessonId입니다.

예: `/api/animals?species=DOG&maxAgeMonths=24&q=mix&purpose=ADOPT`. `q`에 `&`, `+` 등이 있으면 URL 인코딩이 필요하며 화면의 HTTP 클라이언트가 처리합니다. `/api/animals?maxAgeMonths=-1`은 400, `/api/animals?shelterId=sfacc`도 400입니다.

현재 무조건 목록 응답의 처음 한 항목을 발췌하면 아래와 같습니다. 실제 응답의 `items`에는 총 20개가 들어 있습니다.

```json
{
  "items": [
    {
      "id": "mock-sfacc-01",
      "name": "Luna",
      "species": "DOG",
      "breed": "Terrier mix",
      "ageMonths": 24,
      "description": "Luna는 함께 산책하거나 편안한 시간을 보내는 것을 좋아하는 가상의 동물입니다. 학습용으로 만든 소개입니다.",
      "imageUrl": "/assets/dog.svg",
      "shelterId": "sfacc",
      "adoptable": true,
      "fosterable": false,
      "demo": true
    }
  ],
  "total": 20,
  "summary": {
    "shelterCount": null,
    "speciesCounts": null,
    "unfinishedLessons": ["lesson-6", "lesson-7"]
  }
}
```

`items`는 동물 객체 배열, `total`은 필터 적용 후 그 배열의 개수입니다. 검색 성공인데 일치 항목이 없으면 200이고 `items`는 `[]`, `total`은 `0`입니다. 이때도 아래의 `summary`가 포함됩니다. `imageUrl`은 Python UI의 `/assets` 경로이며 Java 서버가 이미지를 제공하지 않습니다. 보호소 객체를 동물 안에 중첩하지 않고 `shelterId`로 별도 목록과 연결합니다.

### Result summary

`summary` describes the exact `items` list after all selected filters. Lesson 6 supplies `shelterCount`, the number of distinct shelter IDs. Lesson 7 supplies `speciesCounts`, with both `DOG` and `CAT` integer keys, including zero for an absent species. Each exercise uses the same filtered list independently.

An unfinished summary metric is `null`, never a fabricated zero. Only its lesson appears in `unfinishedLessons`, ordered lesson-6 then lesson-7. Both unfinished summary stubs leave the catalog available with status 200, as shown above. Completing one exercise immediately exposes its result; for example, one DOG from one shelter with lesson 7 unfinished gives:

```json
{"shelterCount":1,"speciesCounts":null,"unfinishedLessons":["lesson-7"]}
```

If only lesson 6 is unfinished, that same result gives:

```json
{"shelterCount":null,"speciesCounts":{"DOG":1,"CAT":0},"unfinishedLessons":["lesson-6"]}
```

After both exercises are complete, a successful empty result is:

```json
{"items":[],"total":0,"summary":{"shelterCount":0,"speciesCounts":{"DOG":0,"CAT":0},"unfinishedLessons":[]}}
```

A completed nonempty summary for three animals with shelter IDs `sfacc`, `phs`, `sfacc` and species `DOG`, `CAT`, `DOG` is:

```json
{"shelterCount":2,"speciesCounts":{"DOG":2,"CAT":1},"unfinishedLessons":[]}
```

Only `ExerciseNotImplementedException` from a summary exercise becomes a missing metric. An unexpected exception from either summary method still returns the existing sanitized `INTERNAL_ERROR` 500 response and logs the diagnostic stack trace. Unfinished selected filters and ID lookup retain their existing 501 behavior; invalid queries still return 400 before exercises run.

## GET /api/shelters

학생 과제 없이 보호소 5곳을 반환합니다. 쿼리 필터를 제공하지 않습니다. `items` 안의 한 항목 예:

```json
{
  "items": [
    {
      "id": "sfacc",
      "name": "San Francisco Animal Care & Control",
      "city": "San Francisco",
      "websiteUrl": "https://www.sfanimalcare.org/adoptable-animals/",
      "phone": "+1-415-554-6364",
      "sourceUrl": "https://www.sfanimalcare.org/contact/"
    }
  ]
}
```

`websiteUrl`은 일반적인 공식 보호소 안내입니다. 해당 가상 동물의 실제 입양 페이지가 아닙니다. 목록 화면은 이 URL을 동물 이름과 `Visit shelter website`에 연결합니다. `sourceUrl`은 정보 출처이고 `phone`은 대표 연락처입니다.

## GET /api/animals/{id} — 선택 학습 API

수업 3의 `findById`를 호출합니다. 기본 목록 → 공식 보호소 연결에는 필요하지 않습니다. 초기에는 알려진 ID라도 `lesson-3` 501입니다. 학생 구현 후에는 일치 동물 객체 하나를 목록의 `items`/`total` 포장 없이 반환합니다. 예를 들어 `/api/animals/mock-sfacc-01`의 200 응답은 위 Luna 객체 하나입니다. 메서드가 `null`을 반환하면 404입니다. 별도 Python 학습 페이지는 `/animals/mock-sfacc-01`입니다.

## 오류와 상태

오류는 [ApiErrors](../backend/src/main/java/org/royshelter/ApiErrors.java)의 공통 JSON 구조를 사용합니다. `lessonId`는 미완성 과제에만 문자열이고 나머지는 `null`입니다.

| 상태 | code | 의미 |
| --- | --- | --- |
| 200 | 없음 | 목록/보호소/조회 성공. 0개 목록도 성공 |
| 400 | `INVALID_REQUEST` | 동물 목록 쿼리의 알 수 없는 키, 중복 키 또는 잘못된 값 |
| 404 | `NOT_FOUND` | 구현된 ID 조회가 `null` 반환하거나 요청한 경로가 없음 |
| 405 | `METHOD_NOT_ALLOWED` | 경로에서 지원하지 않는 요청 방식. `Allow` 헤더로 지원 방식을 안내 |
| 501 | `EXERCISE_NOT_IMPLEMENTED` | 선택한 필터 또는 ID 조회 TODO 미완성, lessonId로 수업 안내. 요약 수업 6·7 미완성은 200의 summary에 표시 |
| 500 | `INTERNAL_ERROR` | 예상하지 못한 서버 예외. 내부 진단은 응답에서 숨김 |

```json
{"code":"INVALID_REQUEST","message":"알 수 없는 검색 조건입니다.","lessonId":null}
```

```json
{"code":"NOT_FOUND","message":"요청한 정보를 찾을 수 없습니다.","lessonId":null}
```

```json
{"code":"METHOD_NOT_ALLOWED","message":"지원하지 않는 요청 방식입니다.","lessonId":null}
```

```json
{"code":"EXERCISE_NOT_IMPLEMENTED","message":"아직 구현하지 않은 학습 기능입니다. (lesson-4)","lessonId":"lesson-4"}
```

```json
{"code":"INTERNAL_ERROR","message":"요청을 처리하지 못했습니다.","lessonId":null}
```

서버가 꺼져 연결할 수 없으면 HTTP 상태나 위 JSON을 받지 못합니다. [Python 클라이언트](../frontend/api_client.py)는 `BACKEND_UNAVAILABLE`을 만들며, 5초 타임아웃은 `BACKEND_TIMEOUT`으로 구분합니다. 정상 상태의 잘못된 JSON은 `INVALID_RESPONSE`, 공통 오류 JSON이 없는 오류 응답은 상태에 따라 `SERVER_ERROR` 또는 `HTTP_ERROR`입니다. 이들을 빈 목록이나 미완성 수업으로 바꾸지 않습니다. UI에서 Java 실행/Retry 안내를 확인할 수 있습니다.

## 데이터 출처와 교체

[animals.json](../backend/src/main/resources/data/animals.json)의 이름·품종·나이·소개·가능 여부와 보호소 연결은 만든 값이며 모두 `demo: true`입니다. 그림은 로컬 학습용 일러스트입니다. [shelters.json](../backend/src/main/resources/data/shelters.json)은 실제 기관명·도시·대표 번호·공식 HTTPS 링크를 담습니다. 출처와 확인일은 [보호소 정보 출처](data-sources.md)에 기록합니다. 운영 시간, 실제 동물, 신청 조건은 공식 사이트에서 확인하세요.

같은 필드 구조를 유지해 fixture 내용을 새 학습 데이터로 교체할 수 있습니다. [Catalog](../backend/src/main/java/org/royshelter/Catalog.java)는 고유 ID, 연결된 shelterId, 종류, 비음수 나이, demo 값, 로컬 이미지 경로와 보호소별 공식 호스트를 시작 시 검증합니다. 교체 후 기본 테스트를 실행하고 Java를 재시작합니다. 동물 수/예시가 바뀌면 테스트의 기대와 문서도 함께 검토합니다. 새 기관을 추가하려면 출처와 Catalog의 허용 호스트도 교사가 검토합니다. 외부 수집기나 미래 API 어댑터를 미리 만들지는 않습니다.

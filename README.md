# Bay Area Shelter Hub — Java·Python 학습 프로젝트

북부 캘리포니아의 실제 보호소 5곳을 **가상 동물 20마리**로 둘러봅니다. 처음부터 모든 보호소의 동물을 모아 보여주며, 동물 이름 또는 `Visit shelter website`를 누르면 해당 보호소의 공식 사이트가 새 탭으로 열립니다. 보호소를 먼저 선택하는 단계는 없습니다. 실제 입양·임시 보호 신청과 현재 동물 확인은 보호소에서 진행합니다. 이 프로젝트는 보호소와 제휴하지 않습니다.

Java는 검색·조회·결과 요약 규칙, Python/NiceGUI는 입력과 표시를 담당합니다. 미완성 과제를 선택하면 오류 대신 해당 수업 번호를 안내합니다. `Reset`을 누르면 전체 목록으로 돌아옵니다. 서버와 기본 목록은 정답 구현 없이 실행됩니다.

## 준비

- **JDK 21**(JRE만으로는 부족), **Python 3.13**, 코드 편집기, 브라우저가 필요합니다.
- Maven은 별도 설치하지 않습니다. 저장소의 Maven Wrapper가 Maven 3.9.11을 받습니다. 처음 실행할 때 인터넷이 필요합니다.
- Linux에서는 Maven Wrapper 실행 전에 `unzip`을 설치하세요(예: Ubuntu/Debian은 `sudo apt install unzip`).
- Java는 Spring Boot 4.1.2, Python 패키지는 [requirements.txt](frontend/requirements.txt)의 고정 버전을 사용합니다.
- 아래 명령은 모두 프로젝트 최상위 폴더에서 실행합니다. 편집기로 `Roy_SHELTER` 폴더를 연 뒤 터미널을 여세요. macOS/Linux 명령과 Windows PowerShell 명령을 구별하세요.

macOS/Linux:

```sh
pwd
java -version
python3.13 --version
python3.13 -m venv .venv
.venv/bin/python -m pip install -r frontend/requirements.txt
```

Windows PowerShell:

```powershell
Get-Location
java -version
py -3.13 --version
py -3.13 -m venv .venv
.\.venv\Scripts\python.exe -m pip install -r frontend\requirements.txt
```

이미 `.venv`가 있으면 다시 만들 필요가 없습니다. 가상 환경을 활성화하지 않고 위 Python 경로를 직접 사용하므로 PowerShell 실행 정책 변경도 필요 없습니다.

### 프로젝트 안의 JDK를 사용하는 경우

설치한 JDK 21이 이미 선택되어 있으면 이 단계는 건너뜁니다. `.tools`와 `.venv`는 Git에서 제외됩니다. 다운로드한 JDK는 컴퓨터/운영체제에 맞아야 하며 저장소에 포함되지 않습니다.

이 프로젝트 준비 과정에서 macOS용 JDK를 `.tools/jdk-21.0.13+9/Contents/Home`에 풀어 둔 경우, **Java 명령을 실행할 터미널마다** 다음을 실행하세요. 터미널을 닫으면 설정이 끝납니다. 시스템 전체 설정은 바꾸지 않습니다.

```sh
export JAVA_HOME="$PWD/.tools/jdk-21.0.13+9/Contents/Home"
"$JAVA_HOME/bin/java" -version
```

Linux에서 직접 받은 JDK는 `Contents/Home` 대신 실제 `bin` 폴더의 상위 폴더를 `JAVA_HOME`으로 지정하세요. Windows에서 JDK를 `.tools/jdk-21`에 풀었다면:

```powershell
$env:JAVA_HOME = (Resolve-Path .\.tools\jdk-21).Path
& "$env:JAVA_HOME\bin\java.exe" -version
```

## 두 터미널에서 실행

**터미널 1 — Java**: JDK 21을 선택한 뒤 실행하고 그대로 둡니다.

```sh
backend/mvnw -f backend/pom.xml spring-boot:run
```

Windows PowerShell:

```powershell
.\backend\mvnw.cmd -f backend\pom.xml spring-boot:run
```

시작 로그의 `Started ShelterApplication`을 기다립니다. [전체 동물 JSON](http://127.0.0.1:8080/api/animals)에서 `total: 20`, [보호소 JSON](http://127.0.0.1:8080/api/shelters)에서 5곳을 확인할 수 있습니다. JSON은 프로그램끼리 주고받는 데이터 형식입니다. 먼저 Java 연습을 할 때는 자세히 알 필요가 없습니다.

**터미널 2 — Python**: 같은 프로젝트 폴더에서 실행하고 그대로 둡니다.

```sh
.venv/bin/python frontend/app.py
```

Windows PowerShell:

```powershell
.\.venv\Scripts\python.exe frontend\app.py
```

브라우저에서 [학습 화면](http://127.0.0.1:8081/)을 엽니다. 자동으로 브라우저를 열지는 않습니다. Demo 안내, 전체 20마리, 동물 이름을 통한 공식 보호소 연결을 확인하세요. 필터는 수업 1·2·4를 구현해야 동작합니다. 수업 6·7의 결과 요약은 서로 독립적입니다. 하나만 완성해도 해당 보호소 수 또는 DOG/CAT 개수가 표시됩니다. `0`은 완성된 계산의 실제 값이고 미완성 `null`은 해당 수업 안내로 표시됩니다. 수업 5 안내는 페이지에 한 번 나오며 카드는 계속 사용할 수 있습니다. [ID 조회 연습 페이지](http://127.0.0.1:8081/animals/mock-sfacc-01)는 수업 3용 선택 과제이며 기본 탐색 경로에서는 사용하지 않습니다.

각 서버를 종료하려면 해당 터미널에서 **Ctrl-C**를 누릅니다. Python 종료 시 `KeyboardInterrupt` 또는 `CancelledError`가 표시될 수 있으며, Ctrl-C 직후라면 종료 과정의 출력입니다. Java 파일을 저장한 뒤에는 터미널 1에서 Ctrl-C 후 Java 명령을 다시 실행하고 화면에서 검색 또는 새로 고침을 하세요. Python 파일을 바꾸면 터미널 2도 종료 후 재실행하고 브라우저를 새로 고칩니다. 자동 재시작은 사용하지 않습니다.

## 확인 명령과 학습 순서

기본 연결·검증 테스트와 패키지 생성:

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

기본 Java 테스트는 `exercise` 태그를 제외합니다. **기본 테스트 통과는 과제를 풀었다는 뜻이 아닙니다.** 수업별 정답 조건을 확인하려면 별도 명령이 필요합니다. 예를 들어 수업 1:

```sh
backend/mvnw -f backend/pom.xml -Pexercises '-Dtest=AnimalExercisesTest$Lesson1' test
```

Windows PowerShell:

```powershell
.\backend\mvnw.cmd -f backend\pom.xml -Pexercises '-Dtest=AnimalExercisesTest$Lesson1' test
```

처음에는 `ExerciseNotImplementedException`과 `lesson-1` 때문에 실패하는 것이 정상입니다. 작은따옴표는 `$Lesson1`을 셸이 바꾸지 않도록 필요합니다. 전체 학생 과제를 한꺼번에 실행하는 `-Pexercises test`도 처음에는 실패합니다. Python 수업 5도 서버 없이 별도로 검사합니다:

```sh
.venv/bin/python -m pytest frontend/exercises -q
```

Windows PowerShell:

```powershell
.\.venv\Scripts\python.exe -m pytest frontend\exercises -q
```

초기에는 네 조합이 `ExerciseNotImplementedError: lesson-5`로 실패합니다. 기본 Python 검사는 연결과 표시를 검사하고, 위 opt-in 검사는 학생 함수의 결과를 검사합니다. 고정된 테스트 총개수 대신 실행 결과의 통과·실패 원인을 확인하세요.

[학생 가이드](docs/student-guide.md)에서 준비 → 수업 1 → 2 → 4 → 6 → 7 → 5 순서로 진행하세요. 수업 3은 선택이며 수업 5의 선행 조건이 아닙니다. 학생이 수정할 곳은 Java의 `AnimalExercises.java`(수업 1·2·3·4·6·7)와 Python의 `frontend/student_ui.py`(수업 5)뿐입니다. `ExerciseHelpers.normalizeText(String)`와 `ExerciseHelpers.supportedSpecies()`는 제공된 도우미이고, Python 표시 함수는 `frontend/ui_helpers.py`가 제공합니다. [교사용 가이드](docs/teacher-guide.md), [API 규약](docs/api.md), [데이터 출처](docs/data-sources.md)도 제공합니다. 정답 구현은 포함하지 않습니다.

## 실행이 안 될 때

| 증상 | 확인과 조치 |
| --- | --- |
| Java 버전 오류 / `JAVA_HOME` 오류 | `java -version` 또는 `"$JAVA_HOME/bin/java" -version`이 21인지 확인하고 터미널 1에서 다시 설정합니다. |
| Wrapper 권한 오류(macOS/Linux) | `chmod +x backend/mvnw` 후 같은 명령을 다시 실행합니다. |
| Linux에서 Wrapper 체크섬 오류 / 다운로드 파일 손상 경고 | `unzip -v`로 설치 여부를 확인하세요. `unzip`이 없으면 Wrapper가 받은 `.tar.gz`가 ZIP용 체크섬과 달라 실패합니다. `unzip` 설치 후 같은 명령을 다시 실행하세요. |
| 패키지를 못 찾음 | 프로젝트 폴더에서 `.venv`의 Python으로 설치·실행했는지 확인합니다. 첫 다운로드에는 인터넷이 필요합니다. |
| 8080/8081 포트 사용 중 | 이미 실행한 이 프로젝트의 터미널을 찾아 Ctrl-C로 종료합니다. 모르는 프로세스는 종료하지 마세요. 아래 다른 포트 명령을 사용할 수 있습니다. |
| `Java is unavailable` / `BACKEND_UNAVAILABLE` | 터미널 1의 시작 로그와 주소를 확인하고 Java를 실행한 후 화면의 `Retry`를 누릅니다. 목록 0개와 서버 연결 실패는 다릅니다. |
| `Java took too long` / `BACKEND_TIMEOUT` | 클라이언트의 대기 시간은 5초입니다. Java 로그를 확인하고 다시 시도합니다. |
| `Learning exercise · lesson-N` | 연결 실패가 아닙니다. 해당 TODO를 구현하거나 `Reset`으로 전체 목록을 봅니다. |
| 검색 결과 0개 | 요청은 성공했지만 조건에 맞는 동물이 없습니다. `Reset`으로 조건을 지웁니다. |

다른 포트 예시(각각 터미널 1과 2, JDK 설정 후):

```sh
backend/mvnw -f backend/pom.xml spring-boot:run -Dspring-boot.run.arguments=--server.port=18080
SHELTER_API_URL=http://127.0.0.1:18080 FRONTEND_PORT=18081 .venv/bin/python frontend/app.py
```

Windows PowerShell:

```powershell
.\backend\mvnw.cmd -f backend\pom.xml spring-boot:run '-Dspring-boot.run.arguments=--server.port=18080'
$env:SHELTER_API_URL = 'http://127.0.0.1:18080'
$env:FRONTEND_PORT = '18081'
.\.venv\Scripts\python.exe frontend\app.py
```

이 경우 화면 주소도 [18081](http://127.0.0.1:18081/)입니다. 이 설정은 현재 터미널에만 적용됩니다. 기본값으로 돌아가려면 새 터미널을 열거나 PowerShell에서 `Remove-Item Env:SHELTER_API_URL, Env:FRONTEND_PORT`를 실행합니다.

이 저장소는 로컬 학습용입니다. Docker, 클라우드 배포, 로그인, 실제 입양 신청, 실시간 보호소 데이터 수집은 포함하지 않습니다.

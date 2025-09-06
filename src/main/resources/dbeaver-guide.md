# DBeaver에서 H2 데이터베이스 접속 가이드

## 기본 설정 (애플리케이션 실행 중일 때)

1. 애플리케이션 실행 상태 확인
2. DBeaver에서 새 연결 생성
   - 데이터베이스 유형: `H2`
   - 연결 유형: `Network (client/server)`

3. 연결 설정:
   - 호스트: `localhost`
   - 포트: `9092`
   - 데이터베이스: `mem:testdb`
   - 사용자명: `sa` 
   - 비밀번호: (비워둠)

4. JDBC URL로 직접 접속할 경우:
   - `jdbc:h2:tcp://localhost:9092/mem:testdb`

## 문제 해결

### 포트 충돌이 발생하는 경우

다음 명령어로 현재 9092 포트를 사용 중인 프로세스 확인:

```bash
# Windows
netstat -ano | findstr 9092

# Mac/Linux
lsof -i :9092
```
# DBeaver에서 H2 데이터베이스 접속 가이드

이 가이드는 DBeaver를 사용하여 H2 데이터베이스에 접속하는 방법을 단계별로 설명합니다.

## 사전 준비

- DBeaver가 설치되어 있어야 합니다. [DBeaver 다운로드](https://dbeaver.io/download/)
- 스프링 애플리케이션이 실행 중이어야 합니다.

## 접속 단계

### 1. 새 데이터베이스 연결 생성

1. DBeaver를 실행합니다.
2. 메뉴에서 `Database` > `New Database Connection`을 선택합니다.
3. 데이터베이스 선택 대화상자에서 `H2`를 선택하고 `Next`를 클릭합니다.

### 2. 연결 정보 설정

![DBeaver H2 연결 설정](https://dbeaver.io/wp-content/uploads/2015/09/connection-wizard-h2.png)

다음 정보를 입력합니다:

- **JDBC URL**: `jdbc:h2:tcp://localhost:9092/mem:testdb`
- **Username**: `sa`
- **Password**: (비워두기)

### 3. JDBC URL 직접 입력 (대체 방법)

1. `Edit Driver Settings` 버튼을 클릭합니다.
2. `Connection URL` 필드에 다음 URL을 직접 입력합니다:
   ```
   jdbc:h2:tcp://localhost:9092/mem:testdb
   ```

### 4. 연결 테스트

1. `Test Connection` 버튼을 클릭합니다.
2. 성공적으로 연결되면 "Connected" 메시지가 표시됩니다.

### 5. 연결 완료

1. `Finish` 버튼을 클릭하여 연결을 완료합니다.
2. DBeaver 탐색기에 새로운 연결이 표시됩니다.

## 데이터베이스 탐색

1. 생성된 연결을 확장하여 테이블 및 스키마를 볼 수 있습니다.
2. 테이블을 마우스 오른쪽 버튼으로 클릭하고 `View Data`를 선택하여 데이터를 볼 수 있습니다.
3. SQL 편집기에서 직접 쿼리를 실행할 수도 있습니다.

## 문제 해결

- **연결 오류가 발생하는 경우:** 스프링 애플리케이션이 실행 중인지 확인하세요.
- **테이블이 보이지 않는 경우:** 스키마를 선택했는지 확인하세요.
- **JDBC 드라이버 오류:** DBeaver에서 H2 드라이버를 다운로드했는지 확인하세요.

## 참고 사항

- H2는 메모리 데이터베이스이므로 애플리케이션을 재시작하면 데이터가 초기화됩니다.
- 콘솔에 표시된 연결 정보를 참조하여 최신 설정을 확인하세요.
충돌을 해결하려면:
1. 기존 실행 중인 H2 서버 종료
2. application.yml에서 다른 포트 지정
3. H2ServerConfig.java에서 포트 번호 변경

### 내장 콘솔 사용

포트 충돌이 계속되면 내장 H2 콘솔 사용:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`

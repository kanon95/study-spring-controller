# H2 데이터베이스 접속 가이드

## 1. 내장 H2 콘솔 (브라우저)

1. 애플리케이션 실행
2. 브라우저에서 http://localhost:8080/h2-console 접속
3. 연결 정보 입력:
   - JDBC URL: `jdbc:h2:mem:testdb`
   - 사용자명: `sa`
   - 비밀번호: (빈 값으로 둠)
4. 연결 버튼 클릭

## 2. DBeaver에서 접속하기

1. 애플리케이션 실행 (TCP 서버가 시작됨)
2. DBeaver에서 새 연결 생성 (H2 선택)
3. 연결 정보 입력:
   - 연결 유형: `Network (client/server)`
   - 호스트: `localhost`
   - 포트: `9092`
   - 데이터베이스: `mem:testdb`
   - 사용자명: `sa`
   - 비밀번호: (빈 값으로 둠)
4. 연결 테스트 후 완료

## 주의사항

- 애플리케이션이 실행 중이어야 H2 데이터베이스에 접근할 수 있습니다.
- 인메모리 데이터베이스이므로 애플리케이션 종료 시 모든 데이터가 사라집니다.
- TCP 포트(9092)가 다른 프로세스에 의해 사용 중이면 시작에 실패할 수 있습니다.

## 문제 해결
# H2 데이터베이스 접근 가이드

이 가이드는 프로젝트에서 사용 중인 H2 데이터베이스에 접근하는 방법을 설명합니다.

## 1. 브라우저에서 H2 접근하기

### 방법 1: Spring Boot 내장 H2 콘솔

1. 애플리케이션을 실행합니다.
2. 브라우저에서 [http://localhost:8080/h2-console](http://localhost:8080/h2-console)로 접속합니다.
3. 연결 정보를 입력합니다:
   - JDBC URL: `jdbc:h2:tcp://localhost:9092/mem:testdb`
   - 사용자명: `sa`
   - 비밀번호: (비워두기)
4. 「연결」 버튼을 클릭합니다.

### 방법 2: 독립형 H2 콘솔

1. 애플리케이션을 실행합니다.
2. 브라우저에서 [http://localhost:8082](http://localhost:8082)로 접속합니다.
3. 연결 정보를 입력합니다:
   - JDBC URL: `jdbc:h2:tcp://localhost:9092/mem:testdb`
   - 사용자명: `sa`
   - 비밀번호: (비워두기)
4. 「연결」 버튼을 클릭합니다.

## 2. DBeaver에서 H2 접근하기

1. DBeaver를 실행합니다.
2. 「새 데이터베이스 연결」을 클릭합니다.
3. H2 데이터베이스를 선택합니다.
4. 연결 설정을 입력합니다:
   - Connection Type: `Remote (TCP/IP)`
   - Host: `localhost`
   - Port: `9092`
   - Database: `mem:testdb`
   - Username: `sa`
   - Password: (비워두기)
5. 「Test Connection」을 클릭하여 연결을 테스트합니다.
6. 「Finish」를 클릭하여 연결을 저장합니다.

## 주의사항

- 애플리케이션이 실행 중이어야만 H2 데이터베이스에 접속할 수 있습니다.
- 메모리 데이터베이스이므로 애플리케이션이 종료되면 데이터가 초기화됩니다.
- `DB_CLOSE_DELAY=-1` 옵션으로 마지막 연결이 끊어진 후에도 데이터베이스가 유지됩니다.
- 포트 충돌 발생 시: `netstat -ano | findstr 9092` (Windows) 또는 `lsof -i :9092` (Linux/Mac)으로 포트 사용 확인
- 다른 포트 사용을 원할 경우 H2ServerConfig.java와 접속 정보 수정 필요

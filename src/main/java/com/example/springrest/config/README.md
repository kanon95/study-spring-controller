# H2 데이터베이스 설정 안내

## 접속 정보

### 1. 웹 콘솔 접속
- **내장 콘솔**: http://localhost:8080/h2-console
- **독립 콘솔**: http://localhost:8082

### 2. JDBC URL
- **내장 메모리 DB**: jdbc:h2:mem:testdb
- **TCP 접속 URL**: jdbc:h2:tcp://localhost:9092/mem:testdb

### 3. 인증 정보
- **사용자명**: sa
- **비밀번호**: (비어있음)

## DBeaver 접속 설정

1. 새 연결 > H2 선택
2. 다음 정보 입력:
   - **Connection Type**: Network (Client/Server)
   - **Host**: localhost
   - **Port**: 9092
   - **Database**: mem:testdb
   - **Username**: sa
   - **Password**: (비어있음)
3. 전체 JDBC URL: jdbc:h2:tcp://localhost:9092/mem:testdb

## 문제 해결

- 애플리케이션이 시작되지 않는 경우, TCP 포트 9092가 이미 사용 중인지 확인하세요.
- 포트 충돌을 해결하려면 H2ServerConfig.java에서 다른 포트를 설정하세요.
# H2 데이터베이스 설정 문제 해결 가이드

## 문제 증상

1. 애플리케이션이 시작되지 않음
2. "Port already in use" 또는 "Address already in use" 오류 발생
3. H2 콘솔에 접속할 수 없음

## 해결 방법

### 1. 포트 충돌 확인 및 해결

9092 포트가 이미 사용 중인지 확인:

```bash
# Windows
netstat -ano | findstr 9092
taskkill /F /PID [해당_프로세스_ID]

# Mac/Linux
lsof -i :9092
kill -9 [해당_프로세스_ID]
```

### 2. H2 설정 변경

H2ServerConfig.java 파일에서 다른 포트 사용:

```java
return Server.createTcpServer(
    "-tcp", 
    "-tcpAllowOthers", 
    "-tcpPort", "9093"  // 포트 변경
);
```

### 3. TCP 서버 비활성화 방법

TCP 서버가 필요하지 않은 경우 H2ServerConfig.java 파일을 삭제하거나 다음과 같이 변경:

```java
@Configuration
public class H2ServerConfig {
    // TCP 서버 비활성화
}
```

### 4. 다른 프로파일 사용

애플리케이션 실행 시 다음 옵션 추가:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
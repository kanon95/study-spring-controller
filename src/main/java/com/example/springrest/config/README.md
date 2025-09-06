# H2 데이터베이스 설정 및 접속 가이드

## 개요
이 프로젝트는 H2 데이터베이스를 사용하며, 웹 콘솔(H2 Console)과 외부 도구(DBeaver) 모두에서 접속할 수 있도록 설정되어 있습니다.

## H2 서버 구조

### 1. Bean 초기화 순서 (중요!)
Spring에서 H2 관련 Bean들은 다음 순서로 초기화됩니다:

```
1. h2TcpServer (@Order(1)) - 가장 먼저 시작
2. h2WebServer (@Order(2), @DependsOn("h2TcpServer")) - TCP 서버 후 시작
3. DataSource (@DependsOn("h2TcpServer")) - TCP 서버 후 데이터소스 생성
```

**왜 순서가 중요한가?**
- TCP 서버가 먼저 시작되어야 데이터베이스가 생성됨
- 데이터소스는 TCP 서버가 준비된 후 연결 시도
- 웹 서버는 독립적이지만 TCP 서버 후 시작하는 것이 안전

### 2. 서버 포트 구성
- **TCP 서버**: 9092 포트 (DBeaver 접속용)
- **웹 서버**: 8082 포트 (독립 웹 콘솔용)
- **애플리케이션**: 8080 포트 (내장 H2 콘솔 포함)

## 접속 방법

### 1. 웹 콘솔 접속

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

## 설정 파일 구조

### H2ServerConfig.java
```java
@Configuration
public class H2ServerConfig {

    @Bean(name = "h2TcpServer", initMethod = "start", destroyMethod = "stop")
    @Order(1) // 최우선 시작
    public Server h2TcpServer() throws SQLException {
        return Server.createTcpServer(
            "-tcp", "-tcpAllowOthers", "-tcpPort", "9092", "-ifNotExists"
        );
    }

    /* 
     * -ifNotExists 옵션 설명:
     * 1. 데이터베이스 자동 생성: 지정된 데이터베이스가 존재하지 않을 경우 자동으로 생성
     * 2. 오류 방지: 'Database not found' 오류 방지
     * 3. 주의사항: 보안상 운영 환경에서는 비활성화 권장
     * 4. URL 대체: JDBC URL의 'IFEXISTS=FALSE'와 동일한 역할
     * 5. 추가 효과: 파일 기반 DB 사용 시 경로가 없으면 생성
     */

    @Bean(name = "h2WebServer", initMethod = "start", destroyMethod = "stop") 
    @DependsOn("h2TcpServer") // TCP 서버 후 시작
    @Order(2)
    public Server h2WebServer() throws SQLException {
        return Server.createWebServer(
            "-web", "-webAllowOthers", "-webPort", "8082"
        );
    }
}
```

### DataSourceConfig.java
```java
@Configuration
public class DataSourceConfig {

    @Bean
    @DependsOn("h2TcpServer") // TCP 서버가 준비된 후 데이터소스 생성
    public HikariDataSource dataSource(DataSourceProperties properties) {
        // HikariCP 데이터소스 설정
    }
}
```

## 문제 해결

### 1. 포트 충돌 해결

## 해결 방법

### 1. 포트 충돌 해결
```bash
# 포트 9092 사용 중인 프로세스 확인
# Windows
netstat -ano | findstr 9092

# Mac/Linux  
lsof -i :9092

# 프로세스 종료 후 재시작
```

### 2. 데이터베이스 연결 실패
- TCP 서버가 먼저 시작되었는지 확인
- JDBC URL에 `IFEXISTS=FALSE` 옵션 추가
- Bean 의존성 순서 확인 (`@DependsOn`, `@Order`)

### 3. 웹 콘솔 접속 불가
- H2 웹 서버(8082)가 정상 시작되었는지 확인
- 방화벽 설정 확인
- `webAllowOthers` 옵션 활성화 확인

### 4. DBeaver 연결 실패
- TCP 서버(9092)가 활성화되어 있는지 확인
- 연결 유형을 'Network (TCP/IP)'로 설정
- 데이터베이스 이름에 `mem:testdb` 정확히 입력

## 로그 확인 방법

애플리케이션 시작 시 다음과 같은 로그가 나타나야 합니다:
```
H2 TCP Server starting on port 9092...
H2 Web Server starting on port 8082...
HikariPool-1 - Starting...
Database created successfully!
```

## H2 데이터베이스 중요 옵션 설명

### 1. `-ifNotExists` 옵션 (TCP 서버)

```java
Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092", "-ifNotExists")
```

**기능**:
- 데이터베이스가 존재하지 않을 경우 자동 생성
- 'Database not found' 에러 방지
- 특히 메모리 DB나 새 파일 기반 DB 첫 실행 시 필수

**동작 방식**:
- TCP 서버 시작 시 해당 경로에 데이터베이스 존재 여부 확인
- 없으면 새로 생성 (파일 기반인 경우 디렉토리도 생성)
- 메모리 DB의 경우 서버 시작과 함께 초기화

**JDBC URL 대안**:
```
jdbc:h2:tcp://localhost:9092/mem:testdb;IFEXISTS=FALSE
```

**주의사항**:
- 운영 환경에서는 보안상 의도치 않은 DB 생성 방지를 위해 사용 자제
- 테스트/개발 환경에서 유용

## 추가 팁

1. **메모리 DB 특성**: 애플리케이션 종료 시 데이터 소실
2. **영구 저장**: 파일 기반 DB 사용 시 `~/testdb` 경로 설정
3. **성능 최적화**: HikariCP 커넥션 풀 설정 조정
4. **보안**: 운영 환경에서는 `tcpAllowOthers`, `webAllowOthers` 비활성화 권장
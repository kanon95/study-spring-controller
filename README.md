# study-spring-controller
MVC controller vs. REST controller

## Spring Boot REST Controller 예제

이 프로젝트는 Spring Boot를 사용하여 REST API를 구현하는 예제입니다.

### 프로젝트 구조

```
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── springrest/
│   │   │               ├── SpringRestApplication.java
│   │   │               ├── controller/
│   │   │               │   └── UserController.java
│   │   │               ├── model/
│   │   │               │   └── User.java[README.md](README.md)
│   │   │               ├── repository/
│   │   │               │   └── UserRepository.java
│   │   │               └── service/
│   │   │                   └── UserService.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── data.sql
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── springrest/
│                       └── SpringRestApplicationTests.java
```

### 주요 기능

- 사용자(User) 엔티티의 CRUD 기능을 제공하는 REST API
- Spring Data JPA를 사용한 데이터 접근
- H2 인메모리 데이터베이스 사용
- 입력값 유효성 검사

### REST API 엔드포인트

| HTTP 메소드 | 엔드포인트                 | 설명                      |
|------------|--------------------------|---------------------------|
| GET        | /api/users               | 모든 사용자 조회           |
| GET        | /api/users/{id}          | ID로 사용자 조회           |
| POST       | /api/users               | 새 사용자 등록             |
| PUT        | /api/users/{id}          | 사용자 정보 수정           |
| DELETE     | /api/users/{id}          | 사용자 삭제                |
| GET        | /api/users/search/email  | 이메일로 사용자 검색        |
| GET        | /api/users/search/name   | 이름으로 사용자 검색        |
| GET        | /api/users/health        | API 상태 확인              |

### 실행 방법

```bash
# 프로젝트 빌드
mvn clean install

# 프로젝트 실행
mvn spring-boot:run
```

애플리케이션이 실행되면 http://localhost:8080 에서 접속할 수 있으며,
H2 데이터베이스 콘솔은 http://localhost:8080/h2-console 에서 접근 가능합니다.

### 테스트 방법

다음은 curl을 사용한 API 테스트 예시입니다:

```bash
# 모든 사용자 조회
curl http://localhost:8080/api/users

# 새 사용자 등록
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"새사용자", "email":"new@example.com", "phone":"010-0000-0000"}'
```
- 상세 API 예시 : src/main/resources/API-RUN.md 참조.
- 


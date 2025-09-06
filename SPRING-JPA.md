# Spring Data JPA 사용 가이드

## 목차

1. [개요](#개요)
2. [프로젝트 환경 설정](#프로젝트-환경-설정)
3. [엔티티 정의](#엔티티-정의)
4. [리포지토리 인터페이스](#리포지토리-인터페이스)
5. [서비스 계층 구현](#서비스-계층-구현)
6. [데이터베이스 초기화](#데이터베이스-초기화)
7. [쿼리 메소드](#쿼리-메소드)
8. [JPQL 쿼리](#jpql-쿼리)
9. [트랜잭션 관리](#트랜잭션-관리)
10. [페이징과 정렬](#페이징과-정렬)
11. [벌크 연산](#벌크-연산)
12. [관계 매핑](#관계-매핑)
13. [문제 해결](#문제-해결)
14. [참고 자료](#참고-자료)

## 개요

Spring Data JPA는 JPA(Java Persistence API)를 쉽게 사용할 수 있도록 도와주는 스프링 프레임워크의 일부입니다. 이 가이드는 Spring Boot 프로젝트에서 Spring Data JPA를 사용하는 방법을 설명합니다.

### 주요 기능

- 데이터 접근 계층 구현 간소화
- 반복적인 CRUD 연산 자동화
- 메소드 이름을 기반으로 한 쿼리 생성
- 페이징 및 정렬 지원
- 사용자 정의 쿼리 지원

## 프로젝트 환경 설정

### 의존성 추가 (Maven)

```xml
<!-- Spring Boot Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- H2 Database -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### application.yml 설정

```yaml
spring:
  # JPA 설정
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop  # 옵션: create, create-drop, update, validate, none
    show-sql: true           # SQL 쿼리 로깅
    properties:
      hibernate:
        format_sql: true     # SQL 포맷팅

  # SQL 초기화 설정
  sql:
    init:
      mode: always           # 항상 초기화 스크립트 실행
      platform: h2           # 플랫폼 지정

  # Hibernate 초기화 후 data.sql 실행
  defer-datasource-initialization: true
```

### JPA 속성 설명

- **hibernate.ddl-auto**
  - `create`: 애플리케이션 시작 시 테이블 삭제 후 재생성
  - `create-drop`: 애플리케이션 시작 시 테이블 생성, 종료 시 삭제
  - `update`: 엔티티와 테이블 구조 비교 후 필요한 변경사항만 적용
  - `validate`: 엔티티와 테이블 구조가 일치하는지 검증만 수행
  - `none`: 아무 작업도 수행하지 않음

- **show-sql**: SQL 쿼리를 로그에 출력
- **format_sql**: SQL 쿼리를 보기 좋게 포맷팅
- **defer-datasource-initialization**: Hibernate 초기화 후 SQL 스크립트 실행

## 엔티티 정의

### User 엔티티 예제

```java
package com.example.springrest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50, message = "이름은 2-50자 사이여야 합니다")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Column(nullable = false, unique = true)
    private String email;

    @Size(max = 20, message = "전화번호는 20자 이하여야 합니다")
    private String phone;

    // 생성자, 게터, 세터 등
}
```

### JPA 어노테이션 설명

- **@Entity**: 클래스가 JPA 엔티티임을 나타냄
- **@Table**: 매핑할 테이블 지정
- **@Id**: 기본 키 필드 지정
- **@GeneratedValue**: 기본 키 생성 전략 지정
- **@Column**: 컬럼 속성 지정 (nullable, unique 등)
- **@NotBlank**, **@Size**, **@Email**: 유효성 검증 어노테이션

## 리포지토리 인터페이스

### 기본 리포지토리 인터페이스

```java
package com.example.springrest.repository;

import com.example.springrest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 추가 메소드 없이도 기본 CRUD 기능 사용 가능
}
```

### JpaRepository 제공 메소드

- **save(entity)**: 엔티티 저장 또는 업데이트
- **findById(id)**: ID로 엔티티 조회
- **findAll()**: 모든 엔티티 조회
- **delete(entity)**: 엔티티 삭제
- **count()**: 엔티티 수 조회
- **existsById(id)**: ID로 엔티티 존재 여부 확인

## 서비스 계층 구현

### UserService 예제

```java
package com.example.springrest.service;

import com.example.springrest.model.User;
import com.example.springrest.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 모든 사용자 조회
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ID로 사용자 조회
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // 사용자 생성
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // 사용자 업데이트
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + id));

        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());

        return userRepository.save(user);
    }

    // 사용자 삭제
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
```

### 서비스 계층 특징

- **@Service**: 서비스 계층 컴포넌트 표시
- **@Autowired**: 의존성 주입
- **@Transactional**: 트랜잭션 관리

## 데이터베이스 초기화

### schema.sql

```sql
-- 테이블 생성
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20)
);
```

### data.sql

```sql
-- 초기 테스트 데이터
INSERT INTO users (name, email, phone) VALUES ('홍길동', 'hong@example.com', '010-1234-5678');
INSERT INTO users (name, email, phone) VALUES ('김철수', 'kim@example.com', '010-9876-5432');
INSERT INTO users (name, email, phone) VALUES ('이영희', 'lee@example.com', '010-5555-1234');
```

## 쿼리 메소드

### 메소드 이름으로 쿼리 생성

```java
public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 사용자 찾기
    Optional<User> findByEmail(String email);

    // 이름으로 사용자 찾기
    List<User> findByName(String name);

    // 이름에 특정 문자열이 포함된 사용자들 찾기
    List<User> findByNameContainingIgnoreCase(String name);

    // 특정 이름과 이메일을 가진 사용자 찾기
    Optional<User> findByNameAndEmail(String name, String email);

    // 이메일이나 전화번호로 사용자 찾기
    List<User> findByEmailOrPhone(String email, String phone);

    // 이메일이 특정 도메인인 사용자들 찾기
    List<User> findByEmailEndingWith(String domain);

    // 이름으로 정렬하여 사용자 찾기
    List<User> findByNameContainingOrderByNameAsc(String name);
}
```

### 지원되는 키워드

| 키워드 | 예제 | SQL 표현 |
|------|------|----------|
| And | findByNameAndEmail | WHERE name = ? AND email = ? |
| Or | findByNameOrEmail | WHERE name = ? OR email = ? |
| Is, Equals | findByName, findByNameIs | WHERE name = ? |
| Between | findByAgeBetween | WHERE age BETWEEN ? AND ? |
| LessThan | findByAgeLessThan | WHERE age < ? |
| GreaterThan | findByAgeGreaterThan | WHERE age > ? |
| IsNull | findByEmailIsNull | WHERE email IS NULL |
| IsNotNull | findByEmailIsNotNull | WHERE email IS NOT NULL |
| Like | findByNameLike | WHERE name LIKE ? |
| NotLike | findByNameNotLike | WHERE name NOT LIKE ? |
| StartingWith | findByNameStartingWith | WHERE name LIKE '?%' |
| EndingWith | findByNameEndingWith | WHERE name LIKE '%?' |
| Containing | findByNameContaining | WHERE name LIKE '%?%' |
| OrderBy | findByAgeOrderByNameDesc | WHERE age = ? ORDER BY name DESC |
| Not | findByNameNot | WHERE name <> ? |
| In | findByAgeIn(Collection) | WHERE age IN (?, ?, ?) |
| NotIn | findByAgeNotIn(Collection) | WHERE age NOT IN (?, ?, ?) |
| True | findByActiveTrue | WHERE active = true |
| False | findByActiveFalse | WHERE active = false |
| IgnoreCase | findByNameIgnoreCase | WHERE UPPER(name) = UPPER(?) |

## JPQL 쿼리

### @Query 어노테이션 사용

```java
public interface UserRepository extends JpaRepository<User, Long> {
    // JPQL을 사용한 커스텀 쿼리
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.name = :name")
    Optional<User> findByEmailAndName(@Param("email") String email, @Param("name") String name);

    // 네이티브 SQL 쿼리 사용
    @Query(value = "SELECT * FROM users WHERE email LIKE %:domain%", nativeQuery = true)
    List<User> findByEmailDomain(@Param("domain") String domain);

    // 업데이트 쿼리
    @Modifying
    @Query("UPDATE User u SET u.phone = :phone WHERE u.id = :id")
    int updatePhone(@Param("id") Long id, @Param("phone") String phone);
}
```

### @Query 어노테이션 특징

- **JPQL**: 객체 지향 쿼리 언어 사용 (엔티티 이름 참조)
- **nativeQuery**: 네이티브 SQL 사용 가능
- **@Param**: 파라미터 바인딩
- **@Modifying**: 수정 쿼리 표시

## 트랜잭션 관리

### @Transactional 어노테이션

```java
@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 읽기 전용 트랜잭션
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 쓰기 트랜잭션
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    // 특정 예외 롤백 설정
    @Transactional(rollbackFor = IllegalArgumentException.class)
    public User updateUser(Long id, User userDetails) {
        // 업데이트 로직
    }

    // 트랜잭션 격리 수준 설정
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void complexOperation() {
        // 복잡한 로직
    }
}
```

### @Transactional 속성

- **readOnly**: 읽기 전용 트랜잭션 (성능 최적화)
- **rollbackFor/noRollbackFor**: 롤백 조건 설정
- **propagation**: 트랜잭션 전파 방식
- **isolation**: 트랜잭션 격리 수준
- **timeout**: 트랜잭션 타임아웃

## 페이징과 정렬

### 페이징 및 정렬 예제

```java
public interface UserRepository extends JpaRepository<User, Long> {
    // 페이징 처리
    Page<User> findAll(Pageable pageable);

    // 이름으로 검색 + 페이징
    Page<User> findByNameContaining(String name, Pageable pageable);

    // 정렬
    List<User> findByNameContaining(String name, Sort sort);
}
```

### 컨트롤러에서 페이징 및 정렬 사용

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Page<User> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = 
            direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        return userService.getAllUsers(
                PageRequest.of(page, size, Sort.by(sortDirection, sortBy)));
    }
}
```

### 서비스에서 페이징 및 정렬 처리

```java
@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<User> searchUsersByName(String name, Pageable pageable) {
        return userRepository.findByNameContaining(name, pageable);
    }
}
```

## 벌크 연산

### 여러 엔티티 일괄 처리

```java
public interface UserRepository extends JpaRepository<User, Long> {
    // 벌크 삭제
    @Modifying
    @Query("DELETE FROM User u WHERE u.email LIKE %:domain%")
    int deleteByEmailDomain(@Param("domain") String domain);

    // 벌크 업데이트
    @Modifying
    @Query("UPDATE User u SET u.phone = :phone WHERE u.id IN :ids")
    int updatePhoneForUsers(@Param("ids") List<Long> ids, @Param("phone") String phone);
}
```

### 서비스에서 벌크 연산 사용

```java
@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public int deleteUsersByDomain(String domain) {
        return userRepository.deleteByEmailDomain(domain);
    }

    @Transactional
    public int updatePhoneForMultipleUsers(List<Long> userIds, String phone) {
        return userRepository.updatePhoneForUsers(userIds, phone);
    }
}
```

## 관계 매핑

### 일대다 관계 예제

```java
@Entity
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    // 생성자, 게터, 세터 등
}

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    // 생성자, 게터, 세터 등
}
```

### 다대다 관계 예제

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @ManyToMany
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // 생성자, 게터, 세터 등
}

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();

    // 생성자, 게터, 세터 등
}
```

### 연관 관계 어노테이션

- **@OneToOne**: 일대일 관계
- **@OneToMany**: 일대다 관계
- **@ManyToOne**: 다대일 관계
- **@ManyToMany**: 다대다 관계
- **@JoinColumn**: 외래 키 컬럼 지정
- **@JoinTable**: 조인 테이블 지정

## 문제 해결

### 일반적인 오류와 해결 방법

1. **LazyInitializationException**
   - 원인: 세션이 닫힌 후 지연 로딩 시도
   - 해결: `@Transactional` 범위 확장 또는 `FetchType.EAGER` 사용

2. **N+1 쿼리 문제**
   - 원인: 연관 엔티티 로딩 시 추가 쿼리 발생
   - 해결: `JOIN FETCH` 또는 `EntityGraph` 사용

3. **영속성 컨텍스트 오염**
   - 원인: 장시간 트랜잭션으로 영속성 컨텍스트에 많은 엔티티 누적
   - 해결: 영속성 컨텍스트 주기적 플러시 및 클리어

4. **순환 참조**
   - 원인: 양방향 관계에서 JSON 직렬화 문제
   - 해결: `@JsonManagedReference`와 `@JsonBackReference` 사용

5. **엔티티 수정 불가**
   - 원인: 영속성 컨텍스트에 없는 엔티티 수정 시도
   - 해결: `save()` 또는 `saveAndFlush()` 사용

### 성능 최적화 팁

1. **인덱스 활용**: 자주 조회하는 필드에 인덱스 설정
2. **페이징 처리**: 대량 데이터 처리 시 페이징 활용
3. **캐싱**: 자주 조회되는 데이터 캐싱
4. **배치 처리**: 대량 데이터 수정/삭제 시 배치 처리
5. **프로젝션**: 필요한 필드만 조회

## 참고 자료

- [Spring Data JPA 공식 문서](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Hibernate 공식 문서](https://hibernate.org/orm/documentation/)
- [Baeldung JPA 튜토리얼](https://www.baeldung.com/the-persistence-layer-with-spring-data-jpa)
- [Spring Data JPA 예제 프로젝트](https://github.com/spring-projects/spring-data-examples)
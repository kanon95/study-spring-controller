package com.example.springrest.repository;

import com.example.springrest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 찾기
    Optional<User> findByEmail(String email);

    // 이름에 특정 문자열이 포함된 사용자들 찾기
    List<User> findByNameContainingIgnoreCase(String name);

    // JPQL을 사용한 커스텀 쿼리
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.name = :name")
    Optional<User> findByEmailAndName(@Param("email") String email, @Param("name") String name);
}

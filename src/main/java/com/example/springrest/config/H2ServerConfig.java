package com.example.springrest.config;

import java.sql.SQLException;

import org.h2.tools.Server;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;

@Configuration
public class H2ServerConfig {

    /**
     * H2 TCP 서버를 시작하여 외부 도구(DBeaver 등)에서 접속 가능하게 함
     * 가장 먼저 시작되어야 함
     */
    @Bean(name = "h2TcpServer", initMethod = "start", destroyMethod = "stop")
    @Order(1) // 가장 높은 우선순위
    public Server h2TcpServer() throws SQLException {
        System.out.println("H2 TCP Server starting on port 9092...");
        return Server.createTcpServer(
                "-tcp",
                "-tcpAllowOthers",
                "-tcpPort", "9092",
                "-ifNotExists"  // 데이터베이스가 없으면 생성
        );
    }

    /**
     * H2 웹 콘솔 서버 (브라우저 접근용)
     * TCP 서버가 시작된 후에 시작
     */
    @Bean(name = "h2WebServer", initMethod = "start", destroyMethod = "stop")
    @DependsOn("h2TcpServer")
    @Order(2)
    public Server h2WebServer() throws SQLException {
        System.out.println("H2 Web Server starting on port 8082...");
        return Server.createWebServer(
                "-web", 
                "-webAllowOthers", 
                "-webPort", "8082"
        );
    }

    /**
     * 서버 시작 확인용 CommandLineRunner
     * 모든 H2 서버가 시작된 후에 실행
     */
    @Bean
    @DependsOn({"h2TcpServer", "h2WebServer"})
    @Order(3)
    public CommandLineRunner h2ServerStatus() {
        return args -> {
            System.out.println("===========================================");
            System.out.println("H2 Database Access Information:");
            System.out.println("===========================================");
            System.out.println("1. Browser Access:");
            System.out.println("   - Spring Boot H2 Console: http://localhost:8080/h2-console");
            System.out.println("   - Standalone H2 Console: http://localhost:8082");
            System.out.println("2. DBeaver/External Tool Access:");
            System.out.println("   - URL: jdbc:h2:tcp://localhost:9092/mem:testdb");
            System.out.println("   - Driver: org.h2.Driver");
            System.out.println("   - Username: sa");
            System.out.println("   - Password: (empty)");
            System.out.println("===========================================");
        };
    }
}


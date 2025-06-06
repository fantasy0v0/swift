package test;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

@SpringBootApplication
public class TestApplication {

  public static void main(String[] args) {
    SpringApplication.run(TestApplication.class, args);
  }

  @Bean
  public DataSource dataSource() {
    PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:16-alpine");
    String PG_LOCATIONS = "classpath:db/pg";
    pg.start();
    HikariConfig config = new HikariConfig();
    config.setDriverClassName(pg.getDriverClassName());
    config.setJdbcUrl(pg.getJdbcUrl());
    config.setUsername(pg.getUsername());
    config.setPassword(pg.getPassword());
    DataSource dataSource = new HikariDataSource(config);
    Flyway flyway = Flyway.configure()
      .dataSource(dataSource)
      .locations(PG_LOCATIONS)
      .load();
    flyway.migrate();
    Runtime.getRuntime().addShutdownHook(new Thread(pg::stop));
    JDBC.setContext(JDBC.newContext(dataSource));
    return dataSource;
  }
}

package test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DataSourceUtil {

  private final static Logger log = LoggerFactory.getLogger(DataSourceUtil.class);

  public static DataSource create() throws SQLException {
    HikariConfig config = new HikariConfig();
    config.setDriverClassName("org.h2.Driver");
    config.setJdbcUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
    HikariDataSource dataSource = new HikariDataSource(config);
    try (Connection connection = dataSource.getConnection()) {
      try (Statement statement = connection.createStatement()) {
        boolean executed = statement.execute("""
          drop table if exists student;
          """);
        log.debug("ddl: {}", executed);
        executed = statement.execute("""
          CREATE TABLE student (
              id     bigint NOT NULL,
              name   text NOT NULL,
            status INT NOT NULL,
              ext    text
          );
          """);
        log.debug("ddl: {}", executed);
        statement.execute("""
          insert into student(id, name, status) values
          (1, '小明', 0),
          (2, '张三', 1),
          (3, '李四', 2),
          (4, '董超', 2),
          (5, '薛霸', 2);
          """);
        log.debug("dml: {}, {}, {}", executed, statement.getMoreResults(), statement.getUpdateCount());
      }
    }
    return dataSource;
  }

  public static HikariDataSource createPg() throws SQLException {
    HikariConfig config = new HikariConfig();
    config.setDriverClassName("org.postgresql.Driver");
    config.setJdbcUrl("jdbc:postgresql://172.16.8.2:5432/test");
    config.setUsername("test");
    config.setPassword("test");
    HikariDataSource dataSource = new HikariDataSource(config);
    try (Connection connection = dataSource.getConnection()) {
      try (Statement statement = connection.createStatement()) {
        boolean executed = statement.execute("""
          drop table if exists student;
          """);
        log.debug("ddl: {}", executed);
        executed = statement.execute("""
          CREATE TABLE student (
              id     bigint NOT NULL,
              name   text NOT NULL,
              status INT NOT NULL,
              ext    text
            );
          """);
        log.debug("ddl: {}", executed);
        statement.execute("""
          insert into student(id, name, status) values
            (1, '小明', 0),
            (2, '张三', 1),
            (3, '李四', 2),
            (4, '董超', 2),
            (5, '薛霸', 2);
          """);
        log.debug("dml: {}, {}, {}", executed, statement.getMoreResults(), statement.getUpdateCount());
      }
    }
    return dataSource;
  }

}

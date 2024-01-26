package test;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static com.github.fantasy0v0.swift.jdbc.JDBC.select;

class JDBCTest {

  private final Logger log = LoggerFactory.getLogger(JDBCTest.class);

  private static HikariDataSource dataSource;

  @BeforeAll
  static void beforeAll() throws SQLException {
    dataSource = DataSourceUtil.create();
    JDBC.configuration(dataSource);
  }

  @AfterAll
  static void afterAll() {
    if (null != dataSource) {
      dataSource.close();
    }
  }

  @Test
  void testStatement() {
    log.debug("test");
    select("""
      select * from student;
    """).fetch();
  }

}

package test;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class UpdateTest {

  private final Logger log = LoggerFactory.getLogger(UpdateTest.class);

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
  void test() throws SQLException {
    int executed = JDBC.modify("""
        update student set name = ? where id = ?
      """).execute("测试修改", 1);
    Assertions.assertEquals(1, executed);

    try (var dataSource1 = DataSourceUtil.createPg()) {
      JDBC.configuration(dataSource1);

      executed = JDBC.modify("""
        update student set name = ? where id = ?
      """).execute("测试修改", 1);
      Assertions.assertEquals(1, executed);

      Object[] result = JDBC.modify("""
        update student set name = ? where id = ? returning id
      """).fetch("测试修改1", 1);
      Assertions.assertEquals(1, result.length);
      Assertions.assertEquals(1L, result[0]);
    } finally {
      JDBC.configuration(dataSource);
    }
  }

}

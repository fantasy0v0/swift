package test;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.ContainerUtil;
import test.container.JdbcContainer;

import javax.sql.DataSource;

import static com.github.fantasy0v0.swift.jdbc.JDBC.select;

class JDBCTest {

  private final Logger log = LoggerFactory.getLogger(JDBCTest.class);

  private final static JdbcContainer container = JdbcContainer.create(
    ContainerUtil.PG, ContainerUtil.PG_LOCATIONS
  );

  @BeforeAll
  static void beforeAll() {
    DataSource dataSource = container.start();
    JDBC.configuration(dataSource);
  }

  @AfterAll
  static void afterAll() {
    container.stop();
  }

  @Test
  void testStatement() {
    log.debug("test");
    select("""
      select * from student;
    """).fetch();
  }

}

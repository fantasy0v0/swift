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
import java.util.List;

import static com.github.fantasy0v0.swift.jdbc.JDBC.select;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    List<Object[]> data = select("""
      select * from student;
    """).fetch();
    log.debug("data size: {}", data.size());
    assertTrue(data.size() > 2);

    data = select("""
        select * from student
      """).setQueryTimeout(3).setFetchSize(1).setMaxRows(2).fetch();
    log.debug("data size: {}", data.size());
    assertEquals(2, data.size());
  }

}

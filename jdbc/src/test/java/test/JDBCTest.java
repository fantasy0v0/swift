package test;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.SwiftJdbcExtension;

import javax.sql.DataSource;
import java.util.List;

import static com.github.fantasy0v0.swift.jdbc.JDBC.select;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SwiftJdbcExtension.class)
class JDBCTest {

  private final Logger log = LoggerFactory.getLogger(JDBCTest.class);

  @TestTemplate
  void testStatement(DataSource dataSource) {
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

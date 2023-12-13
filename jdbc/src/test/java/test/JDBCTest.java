package test;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import java.sql.SQLException;

import static com.github.fantasy0v0.swift.jdbc.JDBC.select;

class JDBCTest {

  private final Logger log = LoggerFactory.getLogger(JDBCTest.class);

  @Test
  void testStatement() throws SQLException {
    DataSource dataSource = DataSourceUtil.create();
    JDBC.configuration(dataSource);
    log.debug("test");
    select("""
      select * from student;
    """).fetch();

  }

}

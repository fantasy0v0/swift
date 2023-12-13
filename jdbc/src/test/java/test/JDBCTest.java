package test;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.fantasy0v0.swift.jdbc.JDBC.select;

class JDBCTest {

  @Test
  void testStatement() {
    select("""
      select * from users;
    """).fetchOne(null);

  }

}

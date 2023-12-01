package test;

import org.junit.jupiter.api.Test;

import static com.github.fantasy0v0.swift.jdbc.JdbcUtils.select;

class JdbcUtilsTest {

  @Test
  void testStatement() {

    select("""
      select * from users;
    """).fetchOne();

  }
}

package swift.core.test.jdbc;

import org.junit.jupiter.api.Test;

import static com.github.fantasy0v0.swift.core.jdbc.JdbcUtils.*;

import static org.junit.jupiter.api.Assertions.*;

class JdbcUtilsTest {

  @Test
  void testStatement() {

    select("""
      select * from users;
    """).fetchOne();

  }
}

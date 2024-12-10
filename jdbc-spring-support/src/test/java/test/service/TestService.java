package test.service;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import com.github.fantasy0v0.swift.jdbc.exception.SwiftException;
import org.junit.jupiter.api.Assertions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestService {

  @Transactional
  public void test(boolean throwError) {
    int result = JDBC.update("""
    update student set name = ? where id = ?
    """).execute("大明", 1);
    Assertions.assertEquals(1, result);
    String name = JDBC.select("""
    select name from student where id = ?
    """, 1).fetchOne(row -> row.getString(1));
    Assertions.assertEquals("大明", name);
    if (throwError) {
      throw new SwiftException("测试");
    }
  }

}

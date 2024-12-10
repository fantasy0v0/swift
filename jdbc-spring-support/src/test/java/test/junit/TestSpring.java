package test.junit;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import com.github.fantasy0v0.swift.jdbc.exception.SwiftException;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import test.service.TestService;

@Transactional
@SpringBootTest
public class TestSpring {

  @Resource
  private TestService testService;

  @Autowired
  private PlatformTransactionManager transactionManager;

  @Test
  void test() {
    try {
      JDBC.transaction(() -> {
        JDBC.update("""
        update student set name = ? where id = ?
        """).execute("大明", 1);
        throw new SwiftException("测试");
      });
    } catch (SwiftException e) {
      // ignore
    }
    String name = JDBC.select("""
    select name from student where id = ?
    """, 1).fetchOne(row -> row.getString(1));
    Assertions.assertEquals("小明", name);

    JDBC.transaction(() -> {
      JDBC.update("""
      update student set name = ? where id = ?
      """).execute("大明", 1);
    });
    name = JDBC.select("""
    select name from student where id = ?
    """, 1).fetchOne(row -> row.getString(1));
    Assertions.assertEquals("大明", name);
  }

  @Test
  void test2() {
    try {
      testService.test(true);
      Assertions.assertFalse(false);
    } catch (SwiftException e) {
      // ignore
    }
    String name = JDBC.select("""
    select name from student where id = ?
    """, 1).fetchOne(row -> row.getString(1));
    Assertions.assertEquals("小明", name);
  }

}

package test.service;

import com.github.fantasy0v0.swift.jdbc.exception.SwiftException;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Service
public class TestService {

  @Resource
  private DataSource dataSource;

  @Resource
  private JdbcTemplate jdbcTemplate;

  @Transactional
  public void test(boolean throwError) {
    String name = jdbcTemplate.queryForObject(
      "select name from student where id = ?", String.class, 1);
    Assertions.assertEquals("小明", name);
    int result = jdbcTemplate.update(
      "update student set name = ? where id = ?", "大明", 1);
    Assertions.assertEquals(1, result);
//    JDBC.update("""
//    update student set name = ? where id = ?
//    """).execute("大明", 1);
    if (throwError) {
      throw new SwiftException("测试");
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void testRequiresNew(boolean throwError) {
    String name = jdbcTemplate.queryForObject(
      "select name from student where id = ?", String.class, 1);
    Assertions.assertEquals("小明", name);
    int result = jdbcTemplate.update(
      "update student set name = ? where id = ?", "大明", 1);
    Assertions.assertEquals(1, result);
//    JDBC.update("""
//    update student set name = ? where id = ?
//    """).execute("大明", 1);
    if (throwError) {
      throw new SwiftException("测试");
    }
  }
}

package test.service;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import com.github.fantasy0v0.swift.jdbc.exception.SwiftException;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
public class TestService {

  @Resource
  private DataSource dataSource;

  @Resource
  private JdbcTemplate jdbcTemplate;

  @Transactional
  public void test(boolean throwError) {
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

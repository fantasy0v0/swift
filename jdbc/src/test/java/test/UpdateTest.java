package test;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.ContainerUtil;
import test.container.JdbcContainer;
import test.container.JdbcTest;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public class UpdateTest {

  private final Logger log = LoggerFactory.getLogger(UpdateTest.class);

  @TestFactory
  List<DynamicTest> testAllDatabase() {
    return ContainerUtil.testAllContainers(() -> List.of(
      new JdbcTest("test", this::test),
      new JdbcTest("testFetch", this::testFetch),
      new JdbcTest("testArrayParams", this::testArrayParams),
      new JdbcTest("testExecuteBatch", this::testExecuteBatch)
    ));
  }

  void test(DataSource dataSource) throws SQLException {
    int executed = JDBC.update("""
    update student set name = ? where id = ?
    """).execute("测试修改", 1);
    Assertions.assertEquals(1, executed);
    executed = JDBC.update("""
    update student set name = ? where id = ?
    """).execute("测试修改", 1);
    Assertions.assertEquals(1, executed);
  }

  void testFetch(DataSource dataSource) throws SQLException {
    String driverClassName = dataSource.unwrap(HikariDataSource.class).getDriverClassName();
    if (driverClassName.contains("postgresql")) {
      List<Object[]> result = JDBC.update("""
      update student set name = ? where id = ? returning id
      """).fetch("测试修改1", 1);
      Assertions.assertEquals(1, result.size());
      Assertions.assertEquals(1L, result.getFirst()[0]);

      result = JDBC.update("""
      update student set name = ? returning id
      """).fetch("测试修改1");
      Assertions.assertTrue(result.size() > 1);

      Object[] fetchOne1 = JDBC.update("""
      update student set name = ? where id = ? returning id
      """).fetchOne("测试修改2", 1L);
      Assertions.assertEquals(1, fetchOne1.length);
      Assertions.assertEquals(1L, fetchOne1[0]);

      long fetchOne2 = JDBC.update("""
      update student set name = ? where id = ? returning id
      """).fetchOne(row -> row.getLong(1), "测试修改3", 1L);
      Assertions.assertEquals(1L, fetchOne2);
    }
  }

  void testArrayParams(DataSource dataSource) {
    Object[] params = {"测试修改", 1L};
    int executed = JDBC.update("""
    update student set name = ? where id = ?
    """).execute(params);
    Assertions.assertEquals(1, executed);
    Object[] result = JDBC.select("""
      select name, id from student where id = ?
      """, params[1]).fetchOne();
    Assertions.assertEquals(params[0], result[0]);
    Assertions.assertEquals(params[1], result[1]);
  }

  void testExecuteBatch(DataSource dataSource) {
    JDBC.update("""
    update student set name = ? where id = ?
    """).executeBatch(
      List.of(List.of("测试修改1", 1), List.of("测试修改2", 2))
    );
  }

}

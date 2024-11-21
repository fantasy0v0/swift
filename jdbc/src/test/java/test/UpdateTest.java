package test;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.ContainerUtil;
import test.container.JdbcTest;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UpdateTest {

  private final Logger log = LoggerFactory.getLogger(UpdateTest.class);

  @TestFactory
  List<DynamicTest> testAllDatabase() {
    return ContainerUtil.testAllContainers(() -> List.of(
      JdbcTest.of("testExecuteUpdate", this::testExecuteUpdate),
      JdbcTest.of("testFetch", this::testFetch),
      JdbcTest.of("testArrayParams", this::testArrayParams),
      JdbcTest.of("testBatch", this::testBatch),
      JdbcTest.of("testFetchBatch", this::testFetchBatch)
    ));
  }

  void testExecuteUpdate(DataSource dataSource) {
    int executed = JDBC.update("""
    update student set name = ? where id = ?
    """).execute("测试修改", 1);
    Assertions.assertEquals(1, executed);
    executed = JDBC.update("""
    update student set name = ? where id = ?
    """).execute("测试修改1", 1);
    Assertions.assertEquals(1, executed);
    executed = JDBC.update("""
    update student set name = '测试修改3'
    """).execute();
    Assertions.assertTrue(executed > 1);
  }

  void testFetch(DataSource dataSource) throws SQLException {
    String driverClassName = dataSource.unwrap(HikariDataSource.class).getDriverClassName();
    if (driverClassName.contains("postgresql")) {
      List<Object[]> result = JDBC.update("""
      update student set name = ? where id = ? returning id
      """).fetch("测试修改0", 1);
      Assertions.assertEquals(1, result.size());
      Assertions.assertEquals(1L, result.getFirst()[0]);

      result = JDBC.update("""
      update student set name = ? returning id
      """).fetch("测试修改1");
      Assertions.assertTrue(result.size() > 1);

      Object[] fetchOne2 = JDBC.update("""
      update student set name = ? where id = ? returning id, name
      """).fetchOne("测试修改2", 1L);
      Assertions.assertEquals(2, fetchOne2.length);
      Assertions.assertEquals(1L, fetchOne2[0]);
      Assertions.assertEquals("测试修改2", fetchOne2[1]);

      long fetchOne3 = JDBC.update("""
      update student set name = ? where id = ? returning id
      """).fetchOne(row -> row.getLong(1), "测试修改3", 1L);
      Assertions.assertEquals(1L, fetchOne3);
      String actualName = JDBC.select("select name from student where id = ?", 1L)
        .fetchOne(row -> row.getString(1));
      Assertions.assertEquals("测试修改3", actualName);

      Object[] fetchOne4 = JDBC.update("""
      update student set name = '测试修改4' returning id, name
      """).fetchOne();
      Assertions.assertEquals(2, fetchOne4.length);
      Assertions.assertTrue((long)fetchOne4[0] > 1L);
      Assertions.assertEquals("测试修改4", fetchOne4[1]);

      List<Object> fetchOne5Params = new ArrayList<>();
      fetchOne5Params.add("测试修改5");
      fetchOne5Params.add(1L);
      Object[] fetchOne5 = JDBC.update("""
      update student set name = ? where id = ? returning id, name
      """).fetchOne(fetchOne5Params);
      Assertions.assertEquals(2, fetchOne5.length);
      Assertions.assertEquals(1L, fetchOne5[0]);
      Assertions.assertEquals("测试修改5", fetchOne5[1]);

      long fetchOne6 = JDBC.update("""
      update student set name = '测试修改6' where id = 1 returning id
      """).fetchOne(row -> row.getLong(1));
      Assertions.assertEquals(1L, fetchOne6);
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

  void testBatch(DataSource dataSource) {
    int[] executedBatch = JDBC.update("""
      update student set name = ? where id = ?
      """).batch(
      List.of(List.of("测试修改1", 1), List.of("测试修改2", 2))
    );
    Assertions.assertTrue(Arrays.stream(executedBatch).allMatch(i -> i == 1));
  }

  void testFetchBatch(DataSource dataSource) throws SQLException {
    String driverClassName = dataSource.unwrap(HikariDataSource.class).getDriverClassName();
    if (!driverClassName.contains("postgresql")) {
      log.info("针对postgresql的测试, 其他数据库将跳过");
      return;
    }
    List<List<Object>> params = List.of(List.of("测试修改1", 1L), List.of("测试修改2", 2L));
    List<Object[]> executedBatch = JDBC.update("""
    update student set name = ? where id = ? returning name, id
    """).fetchBatch(params);
    Assertions.assertEquals(2, executedBatch.size());
    Assertions.assertEquals(params.get(0).get(0), executedBatch.get(0)[0]);
    Assertions.assertEquals(params.get(0).get(1), executedBatch.get(0)[1]);
    Assertions.assertEquals(params.get(1).get(0), executedBatch.get(1)[0]);
    Assertions.assertEquals(params.get(1).get(1), executedBatch.get(1)[1]);
  }
}

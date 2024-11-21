package test;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.SwiftJdbcExtension;
import test.exception.WorkException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static com.github.fantasy0v0.swift.jdbc.JDBC.transaction;

@ExtendWith(SwiftJdbcExtension.class)
public class InsertTest {

  private final Logger log = LoggerFactory.getLogger(InsertTest.class);

  @TestTemplate
  void test() {
    Assertions.assertThrowsExactly(WorkException.class, () -> {
      transaction(() -> {
        int executed = JDBC.insert("""
          insert into student(id, name, status)
          values(1000, '测试学生', 0)""").execute();
        Assertions.assertEquals(1, executed);

        executed = JDBC.insert("""
          insert into student(id, name, status)
          values(?, ?, ?)""").execute(1001, "测试学生", 0);
        Assertions.assertEquals(1, executed);

        // test null
        executed = JDBC.insert("""
          insert into student(id, name, status, ext)
          values(?, ?, ?, ?)""").execute(1002, "测试学生", 0, null);
        Assertions.assertEquals(1, executed);
        Object[] row = JDBC.select("""
          select ext from student where id = ?""", 1002).fetchOne();
        Assertions.assertEquals(1, row.length);
        Assertions.assertNull(row[0]);

        throw new WorkException();
      });
    });
  }

  @TestTemplate
  void testBatch() {
    List<List<Object>> batchParams = new ArrayList<>();
    batchParams.add(List.of(1000, "测试用户1", 0));
    batchParams.add(List.of(1001, "测试用户2", 1));
    batchParams.add(List.of(1002, "测试用户3", 2));
    batchParams.add(List.of(1003, "测试用户4", 3));
    batchParams.add(List.of(1004, "测试用户5", 4));
    batchParams.add(List.of(1005, "测试用户6", 5));

    int[] executed = JDBC.insert("""
      insert into student(id, name, status)
      values(?, ?, ?)""").batch(batchParams);
    Assertions.assertEquals(6, executed.length);
    for (int i : executed) {
      Assertions.assertEquals(1, i);
    }

    batchParams = new ArrayList<>();
    batchParams.add(List.of("测试用户1", 0));
    batchParams.add(List.of("测试用户2", 1));
    batchParams.add(List.of("测试用户3", 2));
    batchParams.add(List.of("测试用户4", 3));
    batchParams.add(List.of("测试用户5", 4));
    batchParams.add(List.of("测试用户6", 5));
    List<Long> keys = JDBC.insert("""
    insert into swift_user(name, status) values(?, ?)
    """).batch(batchParams, row -> row.getLong(1));
    Assertions.assertEquals(6, executed.length);
    for (long key : keys) {
      Assertions.assertTrue(key > 0);
    }
  }

  @TestTemplate
  void fetch(DataSource dataSource) throws SQLException {
    String driverClassName = dataSource.unwrap(HikariDataSource.class).getDriverClassName();
    if (driverClassName.contains("postgresql")) {
      List<Object[]> result = JDBC.insert("""
        insert into student(id, name, status)
        values(?, ?, ?)
        returning id""").fetch(1000L, "测试学生", 0);
      Assertions.assertEquals(1, result.size());
      Assertions.assertEquals(1000L, result.getFirst()[0]);
    }
  }

  @TestTemplate
  void testDateTime(DataSource dataSource) throws SQLException {
    String driverClassName = dataSource.unwrap(HikariDataSource.class).getDriverClassName();
    if (driverClassName.contains("postgresql")) {
      OffsetDateTime offsetDateTime = OffsetDateTime.of(1500, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
      LocalDateTime localDateTime = LocalDateTime.of(1600, 1, 1, 0, 0, 0, 0);
      List<Object[]> objects = JDBC.insert("""
          insert into datetime_test(id, date)
          values(?, ?) returning date
        """).fetch(1, offsetDateTime);
      log.debug("value: {}", objects.getFirst()[0]);

      objects = JDBC.insert("""
          insert into datetime_test(id, date)
          values(?, ?) returning date
        """).fetch(1, localDateTime);
      log.debug("value: {}", objects.getFirst()[0]);
    }
  }

  @TestTemplate
  void testFetchKey(DataSource dataSource) {
    long key = JDBC.insert("""
    insert into swift_user(name, status) values('测试学生', 0)
    """).fetchKey(row -> row.getLong(1));
    log.debug("key: {}", key);
    Assertions.assertTrue(key > 0);

    key = JDBC.insert("""
    insert into swift_user(name, status) values(?, ?)
    """).fetchKey(row -> row.getLong(1), "测试学生1", 1);
    Assertions.assertTrue(key > 0);

    Object[] row = JDBC.insert("""
    insert into swift_user(name, status) values(?, ?)
    """).fetchKey( "测试学生2", 2);
    // pg会返回整行数据
    Assertions.assertTrue(row.length > 0);
    Assertions.assertTrue(((Number)row[0]).longValue() > 0);

    row = JDBC.insert("""
    insert into swift_user(name, status) values('测试学生3', 2)
    """).fetchKey();
    // pg会返回整行数据
    Assertions.assertTrue(row.length > 0);
    Assertions.assertTrue(((Number)row[0]).longValue() > 0);
  }
}

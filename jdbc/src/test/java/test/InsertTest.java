package test;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.Allowed;
import test.container.Db;
import test.container.SwiftJdbcExtension;
import test.exception.WorkException;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static com.github.fantasy0v0.swift.jdbc.JDBC.transaction;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(1, executed);

        executed = JDBC.insert("""
          insert into student(id, name, status)
          values(?, ?, ?)""").execute(1001, "测试学生", 0);
        assertEquals(1, executed);

        // test null
        executed = JDBC.insert("""
          insert into student(id, name, status, ext)
          values(?, ?, ?, ?)""").execute(1002, "测试学生", 0, null);
        assertEquals(1, executed);
        Object[] row = JDBC.select("""
          select ext from student where id = ?""", 1002).fetchOne();
        assertEquals(1, row.length);
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
    assertEquals(6, executed.length);
    for (int i : executed) {
      assertEquals(1, i);
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
    assertEquals(6, executed.length);
    for (long key : keys) {
      Assertions.assertTrue(key > 0);
    }
  }

  @TestTemplate
  @Allowed(Db.Postgres)
  void fetchWithReturning() {
    List<Object[]> result = JDBC.insert("""
      insert into student(id, name, status)
      values(?, ?, ?)
      returning id""").fetch(1000L, "测试学生", 0);
    assertEquals(1, result.size());
    assertEquals(1000L, result.getFirst()[0]);
  }

  @TestTemplate
  void testDateTime(Db db) {
    if (Db.Postgres == db) {
      OffsetDateTime offsetDateTime = OffsetDateTime.of(2008, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
      LocalDateTime localDateTime = LocalDateTime.of(2022, 1, 1, 0, 0, 0, 0);
      List<Object[]> objects = JDBC.insert("""
      insert into datetime_test(id, date) values(?, ?) returning date
      """).fetch(1, offsetDateTime);
      log.debug("value: {}", objects.getFirst()[0]);

      objects = JDBC.insert("""
      insert into datetime_test(id, date)
      values(?, ?) returning date
      """).fetch(1, localDateTime);
      log.debug("value: {}", objects.getFirst()[0]);
    }
    OffsetDateTime offsetDateTime = OffsetDateTime.of(2008, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    LocalDateTime localDateTime = LocalDateTime.of(2022, 1, 1, 0, 0, 0, 0);

    JDBC.insert("""
    insert into datetime_test(id, date) values(?, ?)
    """).execute(2, offsetDateTime);

    OffsetDateTime result1 = JDBC.select("""
    select date from datetime_test where id = ?
    """, 2).fetchOne(row -> row.getOffsetDateTime(1));

    assertEquals(offsetDateTime, result1.withOffsetSameInstant(ZoneOffset.UTC));

    JDBC.insert("""
    insert into datetime_test(id, date) values(?, ?)
    """).execute(3, localDateTime);

    LocalDateTime result2 = JDBC.select("""
    select date from datetime_test where id = ?
    """, 3).fetchOne(row -> row.getLocalDateTime(1));

    assertEquals(localDateTime, result2);
  }

  @TestTemplate
  void testFetchKey() {
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

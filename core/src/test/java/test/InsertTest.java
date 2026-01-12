package test;

import com.github.fantasy0v0.swift.Row;
import com.github.fantasy0v0.swift.Swift;
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
import java.util.Map;

import static com.github.fantasy0v0.swift.Swift.transaction;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SwiftJdbcExtension.class)
public class InsertTest {

  private final Logger log = LoggerFactory.getLogger(InsertTest.class);

  @TestTemplate
  void test() {
    Assertions.assertThrowsExactly(WorkException.class, () -> {
      transaction(() -> {
        int executed = Swift.insert("""
          insert into student(id, name, status)
          values(1000, '测试学生', 0)""").execute();
        assertEquals(1, executed);

        executed = Swift.insert("""
          insert into student(id, name, status)
          values(?, ?, ?)""").execute(1001, "测试学生", 0);
        assertEquals(1, executed);

        // test null
        executed = Swift.insert("""
          insert into student(id, name, status, ext)
          values(?, ?, ?, ?)""").execute(1002, "测试学生", 0, null);
        assertEquals(1, executed);
        Object[] row = Swift.select("""
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
    batchParams.add(List.of(2000, "测试用户1", 0));
    batchParams.add(List.of(2001, "测试用户2", 1));
    batchParams.add(List.of(2002, "测试用户3", 2));
    batchParams.add(List.of(2003, "测试用户4", 3));
    batchParams.add(List.of(2004, "测试用户5", 4));
    batchParams.add(List.of(2005, "测试用户6", 5));

    int[] executed = Swift.insert("""
      insert into student(id, name, status)
      values(?, ?, ?)""").batch(batchParams);
    assertEquals(6, executed.length);
    for (int i : executed) {
      assertEquals(1, i);
    }

    batchParams = new ArrayList<>();
    batchParams.add(List.of("测试用户11", 0));
    batchParams.add(List.of("测试用户12", 1));
    batchParams.add(List.of("测试用户13", 2));
    batchParams.add(List.of("测试用户14", 3));
    batchParams.add(List.of("测试用户15", 4));
    batchParams.add(List.of("测试用户16", 5));
    List<Long> keys = Swift.insert("""
    insert into swift_user(name, status) values(?, ?)
    """).batch(batchParams, row -> row.getLong(1));
    assertEquals(6, executed.length);
    for (long key : keys) {
      assertTrue(key > 0);
    }
  }

  @TestTemplate
  @Allowed(Db.Postgres)
  void fetchWithReturning() {
    List<Object[]> result = Swift.insert("""
      insert into student(id, name, status)
      values(?, ?, ?)
      returning id""").fetch(2000L, "测试学生", 0);
    assertEquals(1, result.size());
    assertEquals(2000L, result.getFirst()[0]);
  }

  @TestTemplate
  void testLocalDateTime() {
    LocalDateTime localDateTime = LocalDateTime.of(2022, 1, 1, 0, 0, 0, 0);
    Swift.insert("""
      insert into datetime_test(id, date) values(?, ?)
      """).execute(1, localDateTime);

    LocalDateTime result = Swift.select("""
      select date from datetime_test where id = ?
      """, 1).fetchOne(row -> row.getLocalDateTime(1));
    log.debug("result: {}", result);
    assertEquals(localDateTime, result);
  }

  /**
   * MySQL没有像PostgreSQL的timestamptz, 所以只对PostgreSQL执行
   */
  @TestTemplate
  @Allowed(Db.Postgres)
  void testOffsetDateTime() {
    ZoneOffset CST = ZoneOffset.ofHours(8);
    OffsetDateTime offsetDateTime = OffsetDateTime.of(2008, 1, 1, 0, 0, 0, 0, CST);
    Swift.insert("""
      insert into datetime_test(id, date_tz) values(?, ?)
    """).execute(2, offsetDateTime);

    OffsetDateTime result = Swift.select("""
      select date_tz from datetime_test where id = ?
    """, 2).fetchOne(row -> row.getOffsetDateTime(1));
    log.debug("result: {}", result);
    assertTrue(offsetDateTime.isEqual(result));
  }

  @TestTemplate
  void testFetchKey(Db db) {
    long key = Swift.insert("""
      insert into swift_user(name, status) values('测试学生31', 0)
    """).fetchKey(row -> row.getLong(1));
    log.debug("key: {}", key);
    assertTrue(key > 0);

    key = Swift.insert("""
    insert into swift_user(name, status) values(?, ?)
      """).fetchKey(row -> row.getLong(1), "测试学生32", 1);
    assertTrue(key > 0);

    Object[] row = Swift.insert("""
    insert into swift_user(name, status) values(?, ?)
      """).fetchKey("测试学生33", 2);
    // pg会返回整行数据
    // 相关讨论 https://stackoverflow.com/questions/19766816/postgresql-jdbc-getgeneratedkeys-returns-all-columns
    assertTrue(row.length > 0);
    assertTrue(((Number) row[0]).longValue() > 0);
    for (int i = 0; i < row.length; i++) {
      log.debug("column: {} value: {}", i, row[i]);
    }

    Map<String, Object> mapRow = Swift.insert("""
      insert into swift_user(name, status) values('测试学生34', 2)
      """).fetchKey(Row::toMap);
    for (var entry : mapRow.entrySet()) {
      log.debug("column: {} value: {}", entry.getKey(), entry.getValue());
    }

    if (db == Db.Postgres) {
      mapRow = Swift.insert("""
        insert into swift_user(name, status) values('测试学生35', 2) returning id
        """).fetchKey(Row::toMap);
      for (var entry : mapRow.entrySet()) {
        log.debug("column: {} value: {}", entry.getKey(), entry.getValue());
      }
    }
  }
}

package test;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.ContainerUtil;
import test.container.JdbcContainer;
import test.exception.WorkException;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static com.github.fantasy0v0.swift.jdbc.JDBC.transaction;

public class InsertTest {

  private final Logger log = LoggerFactory.getLogger(InsertTest.class);

  private final static JdbcContainer container = JdbcContainer.create(
    ContainerUtil.PG, ContainerUtil.PG_LOCATIONS
  );

  @BeforeAll
  static void beforeAll() {
    DataSource dataSource = container.start();
    JDBC.configuration(dataSource);
  }

  @AfterAll
  static void afterAll() {
    container.stop();
  }

  @Test
  void test() {
    Assertions.assertThrowsExactly(WorkException.class, () -> {
      transaction(() -> {
        int executed = JDBC.modify("""
          insert into student(id, name, status)
          values(1000, '测试学生', 0)""").execute();
        Assertions.assertEquals(1, executed);

        executed = JDBC.modify("""
          insert into student(id, name, status)
          values(?, ?, ?)""").execute(1001, "测试学生", 0);
        Assertions.assertEquals(1, executed);

        // test null
        executed = JDBC.modify("""
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

  @Test
  void testBatch() {
    List<List<Object>> batchParams = new ArrayList<>();
    batchParams.add(List.of(1000, "测试用户1", 0));
    batchParams.add(List.of(1001, "测试用户2", 1));
    batchParams.add(List.of(1002, "测试用户3", 2));
    batchParams.add(List.of(1003, "测试用户4", 3));
    batchParams.add(List.of(1004, "测试用户5", 4));
    batchParams.add(List.of(1005, "测试用户6", 5));

    int[] executed = JDBC.modify("""
      insert into student(id, name, status)
      values(?, ?, ?)""").executeBatch(batchParams);
    Assertions.assertEquals(6, executed.length);
    for (int i : executed) {
      Assertions.assertEquals(1, i);
    }
  }

  @Test
  void fetch() {
    List<Object[]> result = JDBC.modify("""
        insert into student(id, name, status)
        values(?, ?, ?)
        returning id""").fetch(1000L, "测试学生", 0);
    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals(1000L, result.getFirst()[0]);
  }

  @Test
  void testDateTime() {
    OffsetDateTime offsetDateTime = OffsetDateTime.of(1500, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    LocalDateTime localDateTime = LocalDateTime.of(1600, 1, 1, 0, 0, 0, 0);
    List<Object[]> objects = JDBC.modify("""
          insert into datetime_test(id, date)
          values(?, ?) returning date
        """).fetch(1, offsetDateTime);
    log.debug("value: {}", objects.getFirst()[0]);

    objects = JDBC.modify("""
          insert into datetime_test(id, date)
          values(?, ?) returning date
        """).fetch(1, localDateTime);
    log.debug("value: {}", objects.getFirst()[0]);
  }
}

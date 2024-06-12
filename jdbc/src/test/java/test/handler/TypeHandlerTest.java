package test.handler;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import com.github.fantasy0v0.swift.jdbc.type.TypeSetHandler;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.DataSourceUtil;
import test.vo.Student;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.github.fantasy0v0.swift.jdbc.JDBC.select;
import static org.junit.jupiter.api.Assertions.*;

class TypeHandlerTest {

  private final static Logger log = LoggerFactory.getLogger(TypeHandlerTest.class);

  @Test
  void testCustom() throws SQLException {
    try (HikariDataSource dataSource = DataSourceUtil.create()) {
      JDBC.configuration(dataSource);
      JDBC.configuration(new TypeSetHandler<Student>() {
        @Override
        public Class<Student> support() {
          return Student.class;
        }

        @Override
        public void doSet(Connection con, PreparedStatement ps, int index, Student parameter) throws SQLException {
          String value = "%s %s".formatted(parameter.id(), parameter.name());
          ps.setString(index, value);
        }
      });
      String value = select("""
      select ?
      """, new Student(111, "test", 0)).fetchOne(row -> row.getString(1));
      assertEquals("111 test", value);
    }
  }

  @Test
  void testDefault() throws SQLException {
    try (HikariDataSource dataSource = DataSourceUtil.create()) {
      JDBC.configuration(dataSource);

      Boolean p1 = false;
      Byte p2 = (byte) 2;
      Double p3 = (double) 3;
      Float p4 = (float) 4;
      Integer p5 = 5;
      Long p6 = 6L;
      Short p7 = 7;
      String p8 = "test";
      Object p9 = null;
      LocalDateTime p10 = LocalDateTime.now();
      LocalTime p11 = LocalTime.now();
      LocalDate p12 = LocalDate.now();
      OffsetDateTime p13 = OffsetDateTime.now();
      Object[] result = select("""
          select
          ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
          ?, ?, ?,
          now(), current_timestamp,
          now(), current_timestamp
          """,
        p1, p2, p3, p4, p5, p6, p7, p8, p9, p10,
        p11, p12, p13
      ).fetchOne(row -> new Object[] {
        row.getObject(1),
        row.getObject(2),
        row.getObject(3),
        row.getObject(4),
        row.getObject(5),
        row.getObject(6),
        row.getObject(7),
        row.getObject(8),
        row.getObject(9),
        row.getLocalDateTime(10),
        row.getLocalTime(11),
        row.getLocalDate(12),
        row.getOffsetDateTime(13),
        row.getLocalDateTime(14),
        row.getOffsetDateTime(15),
        row.getLocalDateTime(16),
        row.getOffsetDateTime(17)
      });
      assertEquals(p1, result[0]);
      assertEquals(p2.intValue(), result[1]);
      assertEquals(p3, result[2]);
      assertEquals(p4, result[3]);
      assertEquals(p5, result[4]);
      assertEquals(p6, result[5]);
      assertEquals(p7.intValue(), result[6]);
      assertEquals(p8, result[7]);
      assertNull(result[8]);
      // LocalDateTime
      assertEquals(p10, result[9]);
      // LocalTime
      assertInstanceOf(LocalTime.class, result[10]);
      assertEquals(p11.getHour(), ((LocalTime)result[10]).getHour());
      assertEquals(p11.getMinute(), ((LocalTime)result[10]).getMinute());
      assertEquals(p11.getSecond(), ((LocalTime)result[10]).getSecond());
      // LocalDate
      assertEquals(p12, result[11]);
      // OffsetDateTime
      assertEquals(p13, result[12]);
      log.debug("now(): {}", result[13]);
      log.debug("current_timestamp: {}", result[14]);
      log.debug("now(): {}", result[15]);
      log.debug("current_timestamp: {}", result[16]);
    }

  }

  @Test
  void testMap() {
    Map<String, Object> map = new HashMap<>();
    Integer object = (Integer) map.get("123");
    System.out.println(object);
  }

}

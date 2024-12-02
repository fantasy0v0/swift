package test.handler;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import com.github.fantasy0v0.swift.jdbc.type.TypeSetHandler;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.ContainerUtil;
import test.container.JdbcContainer;
import test.container.SwiftJdbcExtension;
import test.vo.Student;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.github.fantasy0v0.swift.jdbc.JDBC.select;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SwiftJdbcExtension.class)
class TypeHandlerTest {

  private final static Logger log = LoggerFactory.getLogger(TypeHandlerTest.class);

  @TestTemplate
  void testCustom() throws SQLException {
    JDBC.configure(new TypeSetHandler<Student>() {
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

  @TestTemplate
  void testDefault(DataSource dataSource) throws SQLException {
    String driverClassName = dataSource.unwrap(HikariDataSource.class).getDriverClassName();

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
      row.getBoolean(1),
      row.getByte(2),
      row.getDouble(3),
      row.getFloat(4),
      row.getInt(5),
      row.getLong(6),
      row.getShort(7),
      row.getString(8),
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
    if (driverClassName.contains("postgresql")) {
      assertEquals(p1, result[0]);
      assertEquals(p2, result[1]);
      assertEquals(p3, result[2]);
      assertEquals(p4, result[3]);
      assertEquals(p5, result[4]);
      assertEquals(p6, result[5]);
      assertEquals(p7, result[6]);
      assertEquals(p8, result[7]);
      assertNull(result[8]);
      // LocalDateTime
      assertInstanceOf(LocalDateTime.class, result[9]);
      var p10_db = (LocalDateTime) result[9];
      assertEquals(p10.getSecond(), p10_db.getSecond());
      assertEquals(p10.getMinute(), p10_db.getMinute());
      assertEquals(p10.getHour(), p10_db.getHour());
      assertEquals(p10.getDayOfMonth(), p10_db.getDayOfMonth());
      assertEquals(p10.getMonth(), p10_db.getMonth());
      assertEquals(p10.getYear(), p10_db.getYear());
      // LocalTime
      assertInstanceOf(LocalTime.class, result[10]);
      assertEquals(p11.getHour(), ((LocalTime)result[10]).getHour());
      assertEquals(p11.getMinute(), ((LocalTime)result[10]).getMinute());
      assertEquals(p11.getSecond(), ((LocalTime)result[10]).getSecond());
      // LocalDate
      assertEquals(p12, result[11]);
      // OffsetDateTime
      assertInstanceOf(OffsetDateTime.class, result[12]);
      var p13_db = (OffsetDateTime) result[12];
      assertEquals(p13.getOffset(), p13_db.getOffset());
      assertEquals(p13.getSecond(), p13_db.getSecond());
      assertEquals(p13.getMinute(), p13_db.getMinute());
      assertEquals(p13.getHour(), p13_db.getHour());
      assertEquals(p13.getDayOfMonth(), p13_db.getDayOfMonth());
      assertEquals(p13.getMonth(), p13_db.getMonth());
      assertEquals(p13.getYear(), p13_db.getYear());
      log.debug("now(): {}", result[13]);
      log.debug("current_timestamp: {}", result[14]);
      log.debug("now(): {}", result[15]);
      log.debug("current_timestamp: {}", result[16]);
    } else if (driverClassName.contains("mysql")) {
      assertEquals(p1, result[0]);
      assertEquals(p2, result[1]);
      assertEquals(p3, result[2]);
      assertEquals(p4, result[3]);
      assertEquals(p5, result[4]);
      assertEquals(p6, result[5]);
      assertEquals(p7, result[6]);
      assertEquals(p8, result[7]);
      assertNull(result[8]);
      // LocalDateTime
      assertInstanceOf(LocalDateTime.class, result[9]);
      var p10_db = (LocalDateTime) result[9];
      assertEquals(p10.getSecond(), p10_db.getSecond());
      assertEquals(p10.getMinute(), p10_db.getMinute());
      assertEquals(p10.getHour(), p10_db.getHour());
      assertEquals(p10.getDayOfMonth(), p10_db.getDayOfMonth());
      assertEquals(p10.getMonth(), p10_db.getMonth());
      assertEquals(p10.getYear(), p10_db.getYear());
      // LocalTime
      assertInstanceOf(LocalTime.class, result[10]);
      assertEquals(p11.getHour(), ((LocalTime)result[10]).getHour());
      assertEquals(p11.getMinute(), ((LocalTime)result[10]).getMinute());
      assertEquals(p11.getSecond(), ((LocalTime)result[10]).getSecond());
      // LocalDate
      assertEquals(p12, result[11]);
      // OffsetDateTime
      assertInstanceOf(OffsetDateTime.class, result[12]);
      var p13_db = (OffsetDateTime) result[12];
      assertEquals(p13.getOffset(), p13_db.getOffset());
      assertEquals(p13.getSecond(), p13_db.getSecond());
      assertEquals(p13.getMinute(), p13_db.getMinute());
      assertEquals(p13.getHour(), p13_db.getHour());
      assertEquals(p13.getDayOfMonth(), p13_db.getDayOfMonth());
      assertEquals(p13.getMonth(), p13_db.getMonth());
      assertEquals(p13.getYear(), p13_db.getYear());
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
    log.debug("object: {}", object);
  }

}

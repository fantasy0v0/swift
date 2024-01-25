package test.handler;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import com.github.fantasy0v0.swift.jdbc.TypeHandler;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import test.DataSourceUtil;
import test.vo.Student;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.github.fantasy0v0.swift.jdbc.JDBC.select;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TypeHandlerTest {

  @Test
  void testCustom() throws SQLException {
    try (HikariDataSource dataSource = DataSourceUtil.create()) {
      JDBC.configuration(dataSource);
      JDBC.configuration(new TypeHandler<Student>() {
        @Override
        public Class<Student> supported() {
          return Student.class;
        }

        @Override
        public boolean handle(Connection conn, PreparedStatement statement, int index, Student parameter) throws SQLException {
          String value = "%s %s".formatted(parameter.id(), parameter.name());
          statement.setString(index, value);
          return true;
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
      Object[] result = select("""
          select
          ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
          ?, ?
          """,
        p1, p2, p3, p4, p5, p6, p7, p8, p9, p10,
        p11, p12
      ).fetchOne();
      assertEquals(p1, result[0]);
      assertEquals(p2.intValue(), result[1]);
      assertEquals(p3, result[2]);
      assertEquals(p4, result[3]);
      assertEquals(p5, result[4]);
      assertEquals(p6, result[5]);
      assertEquals(p7.intValue(), result[6]);
      assertEquals(p8, result[7]);
      assertNull(result[8]);
      assertEquals(Timestamp.valueOf(p10), result[9]);
      assertEquals(Time.valueOf(p11), result[10]);
      assertEquals(Date.valueOf(p12), result[11]);
    }

  }

}

package test.handler;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import com.github.fantasy0v0.swift.jdbc.TypeHandler;
import org.junit.jupiter.api.Test;
import test.DataSourceUtil;
import test.vo.Student;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.github.fantasy0v0.swift.jdbc.JDBC.select;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TypeHandlerTest {

  @Test
  void test() throws SQLException {
    DataSource dataSource = DataSourceUtil.create();
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

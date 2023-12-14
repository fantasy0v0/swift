package test;


import com.github.fantasy0v0.swift.jdbc.JDBC;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.vo.Student;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.fantasy0v0.swift.jdbc.JDBC.select;

class SelectTest {

  private final Logger log = LoggerFactory.getLogger(SelectTest.class);

  @Test
  void testFetch() throws SQLException {
    DataSource dataSource = DataSourceUtil.create();
    JDBC.configuration(dataSource);
    List<Student> students = select("select * from student").fetch(row -> new Student(
      row.getLong(1),
      row.getString(2)
    ));

    for (Student student : students) {
      log.debug("student id:{} name:{}", student.id(), student.name());
    }

    List<Object[]> objects = select("select * from student").fetch();
    for (Object[] array : objects) {
      String row = Arrays.stream(array).map(Object::toString).collect(Collectors.joining(", "));
      log.debug("row: {}", row);
    }
  }
}

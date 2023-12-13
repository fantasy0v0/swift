package test;


import org.junit.jupiter.api.Test;
import test.vo.Student;

import java.util.List;

import static com.github.fantasy0v0.swift.jdbc.JDBC.select;

class SelectTest {

  @Test
  void testFetch() {
    List<Student> students = select("select * from student where status = ?", 1).fetch(row -> {
      return new Student(
        row.getLong(0),
        row.getString(1)
      );
    });
  }
}

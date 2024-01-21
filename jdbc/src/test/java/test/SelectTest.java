package test;


import com.github.fantasy0v0.swift.jdbc.JDBC;
import com.github.fantasy0v0.swift.jdbc.predicate.Predicate;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.vo.Student;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.fantasy0v0.swift.jdbc.JDBC.select;
import static com.github.fantasy0v0.swift.jdbc.clauses.Clauses.where;
import static com.github.fantasy0v0.swift.jdbc.predicate.Predicates.and;
import static com.github.fantasy0v0.swift.jdbc.predicate.Predicates.exp;

class SelectTest {

  private final Logger log = LoggerFactory.getLogger(SelectTest.class);

  @Test
  void testFetch() throws SQLException {
    DataSource dataSource = DataSourceUtil.create();
    JDBC.configuration(dataSource);
    List<Student> students = select("select * from student").fetch(Student::from);

    for (Student student : students) {
      log.debug("student id:{} name:{}", student.id(), student.name());
    }

    List<Object[]> objects = select("select id, name, status from student").fetch();
    for (Object[] array : objects) {
      String row = Arrays.stream(array).map(Object::toString).collect(Collectors.joining(", "));
      log.debug("row: {}", row);
    }

    Object[] row = select("select * from student limit 1").fetchOne();
    Assertions.assertNotNull(row);
  }

  @Test
  void testPredicate() throws SQLException {
    DataSource dataSource = DataSourceUtil.create();
    JDBC.configuration(dataSource);

    String sql = "select * from student";
    List<Object> parameters = new ArrayList<>();
    Predicate predicate = and(
      exp("id > ?", 0),
      exp("status = ?", 2)
    );
    sql = where(sql, predicate);
    parameters.addAll(predicate.getParameters());
    sql += " order by id asc";
    sql += " limit 20";
    List<Student> students = select(sql, parameters)
      .fetch(Student::from);
    log.debug("student size: {}", students.size());
    Assertions.assertTrue(students.stream().allMatch(student -> 2 == student.status()));
  }

  @Test
  void testJson() throws SQLException {
    try(HikariDataSource dataSource = DataSourceUtil.createPg()) {
      JDBC.configuration(dataSource);
      List<String> result = select("""
        select '{ "test": 123}'::jsonb
        """).fetch(row -> {
          return row.getString(1);
        });
      Assertions.assertEquals(1, result.size());
      Assertions.assertEquals("{\"test\": 123}", result.getFirst());
    }
  }
}

package test;


import com.github.fantasy0v0.swift.jdbc.exception.SwiftException;
import com.github.fantasy0v0.swift.jdbc.predicate.Predicate;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.SwiftJdbcExtension;
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

@ExtendWith(SwiftJdbcExtension.class)
class SelectTest {

  private final Logger log = LoggerFactory.getLogger(SelectTest.class);


  @TestTemplate
  void testFetch(DataSource dataSource) {
    List<Student> students = select("select * from student").fetch(Student::from);

    for (Student student : students) {
      log.debug("student id:{} name:{}", student.id(), student.name());
    }

    List<Object[]> objects = select("select id, name, status from student").fetch();
    for (Object[] array : objects) {
      String row = Arrays.stream(array).map(Object::toString).collect(Collectors.joining(", "));
      log.debug("row: {}", row);
    }
  }

  @TestTemplate
  void testFetchOne(DataSource dataSource) {
    Object[] row = select("select * from student limit 1").fetchOne();
    Assertions.assertNotNull(row);
    SwiftException exception = Assertions.assertThrowsExactly(SwiftException.class, () -> {
      select("select * from student").fetchOne();
    });
    Assertions.assertEquals("Expected one result, but found more than one", exception.getMessage());
  }

  @TestTemplate
  void testPredicate(DataSource dataSource) {
    String sql = "select * from student";
    Predicate predicate = and(
      exp("id > ?", 0),
      exp("status = ?", 2)
    );
    sql = where(sql, predicate);
    List<Object> parameters = new ArrayList<>(predicate.getParameters());
    sql += " order by id asc";
    sql += " limit 20 offset 0";
    List<Student> students = select(sql, parameters)
      .fetch(Student::from);
    log.debug("student size: {}", students.size());
    Assertions.assertTrue(students.stream().allMatch(student -> 2 == student.status()));
  }

  @TestTemplate
  void testJson(DataSource dataSource) throws SQLException {
    String driverClassName = dataSource.unwrap(HikariDataSource.class).getDriverClassName();
    if (driverClassName.contains("postgresql")) {
      List<String> result = select("""
      select '{ "test": 123}'::jsonb
      """).fetch(row -> row.getString(1));
      Assertions.assertEquals(1, result.size());
      Assertions.assertEquals("{\"test\": 123}", result.getFirst());
    } else if (driverClassName.contains("mysql")) {
      List<String> result = select("""
      select cast('{"test": 123}' as JSON)
      """).fetch(row -> row.getString(1));
      Assertions.assertEquals(1, result.size());
      Assertions.assertEquals("{\"test\": 123}", result.getFirst());
    }
  }
}

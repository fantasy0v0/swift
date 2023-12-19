package test.predicate;

import com.github.fantasy0v0.swift.jdbc.exception.SwiftJdbcException;
import com.github.fantasy0v0.swift.jdbc.predicate.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.vo.Student;

import java.util.List;

import static com.github.fantasy0v0.swift.jdbc.JDBC.select;
import static com.github.fantasy0v0.swift.jdbc.predicate.Predicates.*;

public class PredicateTest {

  private final Logger log = LoggerFactory.getLogger(PredicateTest.class);

  @Test
  void test() {
    String sql = """
    select * from student
    """;
    Predicate predicate = conjunction();
    predicate = and(
      predicate,
      exp("status = ?", 1),
      exp("isDel = false"),
      or(
        exp("age > ?", 18),
        exp("money > ?", 100)
      ),
      in("id", List.of(1, 2, 3))
    );
    sql += predicate.toSQL();
    List<Object> parameters = predicate.getParameters();
    List<Student> students = select(sql, parameters).fetch(row -> new Student(
      row.getLong(1),
      row.getString(2)
    ));
  }

  @Test
  void inTest() {
    Predicate predicate = in("id", List.of(1));
    String sql = predicate.toSQL();
    List<Object> parameters = predicate.getParameters();
    log.info("sql: {}", sql);
    Assertions.assertEquals("id in(?)", sql);
    Assertions.assertEquals(1, parameters.size());

    predicate = in("id", 1, 2, 3);
    sql = predicate.toSQL();
    parameters = predicate.getParameters();
    log.info("sql: {}", sql);
    Assertions.assertEquals("id in(?,?,?)", sql);
    Assertions.assertEquals(3, parameters.size());

    Assertions.assertThrows(SwiftJdbcException.class, () -> {
      in("id").toSQL();
    });

    predicate = notIn("id", 1, 2, 3);
    sql = predicate.toSQL();
    parameters = predicate.getParameters();
    log.info("sql: {}", sql);
    Assertions.assertEquals("id not in(?,?,?)", sql);
    Assertions.assertEquals(3, parameters.size());
  }

}

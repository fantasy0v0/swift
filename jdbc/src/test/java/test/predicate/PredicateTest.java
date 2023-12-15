package test.predicate;

import com.github.fantasy0v0.swift.jdbc.predicate.Predicate;
import org.junit.jupiter.api.Test;
import test.vo.Student;

import java.util.ArrayList;
import java.util.List;

import static com.github.fantasy0v0.swift.jdbc.JDBC.select;
import static com.github.fantasy0v0.swift.jdbc.predicate.Predicates.*;
import static com.github.fantasy0v0.swift.jdbc.predicate.Predicates.exp;

public class PredicateTest {

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
      )
    );
    sql += predicate.toSQL();
    List<Object> parameters = predicate.getParameters();
    List<Student> students = select(sql, parameters).fetch(row -> new Student(
      row.getLong(1),
      row.getString(2)
    ));
  }

}

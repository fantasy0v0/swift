package test.predicate;

import com.github.fantasy0v0.swift.jdbc.predicate.Predicate;
import org.junit.jupiter.api.Test;
import static com.github.fantasy0v0.swift.jdbc.predicate.Predicates.*;
import static com.github.fantasy0v0.swift.jdbc.predicate.Predicates.exp;

public class PredicateTest {

  @Test
  void test() {
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
    predicate.toSQL();
  }

}

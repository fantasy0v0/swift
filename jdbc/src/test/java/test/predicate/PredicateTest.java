package test.predicate;

import com.github.fantasy0v0.swift.jdbc.exception.SwiftException;
import com.github.fantasy0v0.swift.jdbc.predicate.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.github.fantasy0v0.swift.jdbc.predicate.Predicates.*;

public class PredicateTest {

  private final Logger log = LoggerFactory.getLogger(PredicateTest.class);

  @Test
  void expTest() {
    String expression = "id = ?";
    Predicate predicate = exp(expression, 1);
    Assertions.assertEquals(expression, predicate.toSQL());
  }

  @Test
  void andTest() {
    Predicate predicate = and(
      conjunction(),
      exp("id = 1"),
      exp("status > 0")
    );
    log.debug("sql:{}", predicate.toSQL());
    Assertions.assertEquals("id = 1 and status > 0", predicate.toSQL());
    Assertions.assertEquals(0, predicate.getParameters().size());

    predicate = and(
      conjunction(),
      exp("id = 1"),
      and(
        exp("status > 5"),
        exp("status <= 10")
      )
    );
    log.debug("sql:{}", predicate.toSQL());
    Assertions.assertEquals("id = 1 and status > 5 and status <= 10", predicate.toSQL());
    Assertions.assertEquals(0, predicate.getParameters().size());

    predicate = and(
      conjunction(),
      exp("id = 1"),
      or()
    );
    log.debug("sql:{}", predicate.toSQL());
    Assertions.assertEquals("id = 1", predicate.toSQL());
    Assertions.assertEquals(0, predicate.getParameters().size());

    predicate = and(
      conjunction(),
      exp("id = 1"),
      or(
        exp("status > 5")
      )
    );
    log.debug("sql:{}", predicate.toSQL());
    Assertions.assertEquals("id = 1 and status > 5", predicate.toSQL());
    Assertions.assertEquals(0, predicate.getParameters().size());
  }

  @Test
  void orTest() {
    Predicate predicate = or(
      conjunction(),
      exp("id = 1"),
      exp("status > 0")
    );
    log.debug("sql:{}", predicate.toSQL());
    Assertions.assertEquals("id = 1 or status > 0", predicate.toSQL());
    Assertions.assertEquals(0, predicate.getParameters().size());

    predicate = or(
      conjunction(),
      exp("id = 1"),
      or(
        exp("status > 5"),
        exp("status <= 10")
      )
    );
    log.debug("sql:{}", predicate.toSQL());
    Assertions.assertEquals("id = 1 or status > 5 or status <= 10", predicate.toSQL());
    Assertions.assertEquals(0, predicate.getParameters().size());

    predicate = or(
      conjunction(),
      exp("id = 1"),
      and()
    );
    log.debug("sql:{}", predicate.toSQL());
    Assertions.assertEquals("id = 1", predicate.toSQL());
    Assertions.assertEquals(0, predicate.getParameters().size());

    predicate = or(
      conjunction(),
      exp("id = 1"),
      and(
        exp("status > 5")
      )
    );
    log.debug("sql:{}", predicate.toSQL());
    Assertions.assertEquals("id = 1 or status > 5", predicate.toSQL());
    Assertions.assertEquals(0, predicate.getParameters().size());
  }

  @Test
  void andorTest() {
    Predicate predicate = and(
      conjunction(),
      exp("id = 1"),
      or(
        exp("status > 5"),
        exp("status <= 10")
      )
    );
    log.debug("sql:{}", predicate.toSQL());
    Assertions.assertEquals("id = 1 and (status > 5 or status <= 10)", predicate.toSQL());
    Assertions.assertEquals(0, predicate.getParameters().size());

    predicate = or(
      conjunction(),
      exp("id = 1"),
      and(
        exp("status > 5"),
        exp("status <= 10")
      )
    );
    log.debug("sql:{}", predicate.toSQL());
    Assertions.assertEquals("id = 1 or (status > 5 and status <= 10)", predicate.toSQL());
    Assertions.assertEquals(0, predicate.getParameters().size());
  }

  @Test
  void inTest() {
    Predicate predicate = in("id", List.of(1, 2));
    String sql = predicate.toSQL();
    List<Object> parameters = predicate.getParameters();
    log.info("sql: {}", sql);
    Assertions.assertEquals("id in(?,?)", sql);
    Assertions.assertEquals(2, parameters.size());

    predicate = in("id", 1, 2, 3);
    sql = predicate.toSQL();
    parameters = predicate.getParameters();
    log.info("sql: {}", sql);
    Assertions.assertEquals("id in(?,?,?)", sql);
    Assertions.assertEquals(3, parameters.size());

    Assertions.assertThrows(SwiftException.class, () -> {
      in("id").toSQL();
    });

    predicate = notIn("id", 1, 2, 3);
    sql = predicate.toSQL();
    parameters = predicate.getParameters();
    log.info("sql: {}", sql);
    Assertions.assertEquals("id not in(?,?,?)", sql);
    Assertions.assertEquals(3, parameters.size());

    predicate = and(
      exp("role = ?", 0),
      in("id", List.of(1, 2, 3))
    );
    sql = predicate.toSQL();
    parameters = predicate.getParameters();
    log.info("sql: {}", sql);
    Assertions.assertEquals("role = ? and id in(?,?,?)", sql);
    Assertions.assertEquals(4, parameters.size());
  }

}

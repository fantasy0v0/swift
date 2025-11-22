package test.parameter;

import com.github.fantasy0v0.swift.jdbc.Context;
import com.github.fantasy0v0.swift.jdbc.parameter.ParameterSetter;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.SwiftJdbcExtension;
import test.parameter.vo.Animal;
import test.parameter.vo.Cat;
import test.parameter.vo.Dog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

import static com.github.fantasy0v0.swift.jdbc.JDBC.select;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author fan 2025/10/14
 */
@ExtendWith(SwiftJdbcExtension.class)
public class SetterTest {

  private static final Logger log = LoggerFactory.getLogger(SetterTest.class);

  @TestTemplate
  void testCustom(Context context) {
    context.configure(new ParameterSetter<Animal>() {
      @Override
      public Set<Class<? extends Animal>> support() {
        return Set.of(Cat.class, Dog.class);
      }

      @Override
      public void set(Connection connection, Animal parameter, PreparedStatement statement, int columnIndex) throws SQLException {
        String value;
        if (parameter instanceof Cat cat) {
          value = "%s Cat".formatted(cat.name);
        } else if (parameter instanceof Dog dog) {
          value = "%s Dog".formatted(dog.name);
        } else {
          value = "Unknown";
        }
        statement.setString(columnIndex, value);
      }
    });
    String value = select("""
      select ?
      """, new Cat("Tom")).fetchOne(row -> row.getString(1));
    assertEquals("Tom Cat", value);
    value = select("""
      select ?
      """, new Dog("Spike")).fetchOne(row -> row.getString(1));
    assertEquals("Spike Dog", value);
  }
}

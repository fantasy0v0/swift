package test.type;

import com.github.fantasy0v0.swift.jdbc.type.BooleanTypeHandler;
import com.github.fantasy0v0.swift.jdbc.type.TypeSetHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractTypeHandlerTest {

  @Test
  void test() {
    BooleanTypeHandler typeHandler = new BooleanTypeHandler();
    // assertEquals(Boolean.class, ((TypeGetHandler<?>) typeHandler).support());
    assertEquals(Boolean.class, ((TypeSetHandler<?>) typeHandler).support());
  }

}

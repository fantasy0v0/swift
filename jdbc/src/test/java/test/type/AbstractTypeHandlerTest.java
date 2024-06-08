package test.type;

import com.github.fantasy0v0.swift.jdbc.type.BooleanTypeHandler;
import com.github.fantasy0v0.swift.jdbc.type.TypeGetHandler;
import com.github.fantasy0v0.swift.jdbc.type.TypeSetHandler;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AbstractTypeHandlerTest {

  private final static Logger log = LoggerFactory.getLogger(AbstractTypeHandlerTest.class);

  @Test
  void test() {
    BooleanTypeHandler typeHandler = new BooleanTypeHandler();
    assertEquals(Boolean.class, ((TypeGetHandler<?>) typeHandler).support());
    assertEquals(Boolean.class, ((TypeSetHandler<?>) typeHandler).support());
  }

}

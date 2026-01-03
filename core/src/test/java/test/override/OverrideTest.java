package test.override;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class OverrideTest {

  private final Logger log = LoggerFactory.getLogger(OverrideTest.class);

  public static String t1(List<Object> params) {
    return "List";
  }

  public static String t1(Object... params) {
    return "varargs";
  }

  @Test
  void test() {
    Assertions.assertEquals("List", t1(new ArrayList<>()));
    var data1 = new ArrayList<>();
    data1.add("test");
    data1.add(2);
    Assertions.assertEquals("List", t1(data1));
    Assertions.assertEquals("varargs", t1(1, 2, 3));
  }

}

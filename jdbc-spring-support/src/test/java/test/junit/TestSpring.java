package test.junit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import test.TestApplication;

public class TestSpring {

  @Test
  void test() {
    SpringApplication.run(TestApplication.class);
  }

}

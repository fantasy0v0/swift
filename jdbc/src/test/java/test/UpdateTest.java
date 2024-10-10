package test;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.ContainerUtil;
import test.container.JdbcContainer;

import javax.sql.DataSource;
import java.util.List;

public class UpdateTest {

  private final Logger log = LoggerFactory.getLogger(UpdateTest.class);

  private final static JdbcContainer container = JdbcContainer.create(
    ContainerUtil.PG, ContainerUtil.PG_LOCATIONS
  );

  @BeforeAll
  static void beforeAll() {
    DataSource dataSource = container.start();
    JDBC.configuration(dataSource);
  }

  @AfterAll
  static void afterAll() {
    container.stop();
  }

  @Test
  void test() {
    int executed = JDBC.modify("""
        update student set name = ? where id = ?
      """).execute("测试修改", 1);
    Assertions.assertEquals(1, executed);
    executed = JDBC.modify("""
        update student set name = ? where id = ?
      """).execute("测试修改", 1);
    Assertions.assertEquals(1, executed);

    List<Object[]> result = JDBC.modify("""
        update student set name = ? where id = ? returning id
      """).fetch("测试修改1", 1);
    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals(1L, result.getFirst()[0]);

    result = JDBC.modify("""
          update student set name = ? returning id
        """).fetch("测试修改1");
    Assertions.assertTrue(result.size() > 1);
  }

  @Test
  void testArrayParams() {
    Object[] params = {"测试修改", 1};
    int executed = JDBC.modify("""
        update student set name = ? where id = ?
      """).execute(params);
    Assertions.assertEquals(1, executed);
  }

}

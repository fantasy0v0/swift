package test;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;

public class UpdateTest {

  private final Logger log = LoggerFactory.getLogger(UpdateTest.class);

  @Test
  void test() throws SQLException {
    DataSource dataSource = DataSourceUtil.create();
    JDBC.configuration(dataSource);

    int executed = JDBC.modify("""
        update student set name = ? where id = ?
      """).execute("测试修改", 1);
    Assertions.assertEquals(1, executed);

    dataSource = DataSourceUtil.createPg();
    JDBC.configuration(dataSource);

    executed = JDBC.modify("""
        update student set name = ? where id = ?
      """).execute("测试修改", 1);
    Assertions.assertEquals(1, executed);

    Object[] result = JDBC.modify("""
        update student set name = ? where id = ? returning id
      """).fetch("测试修改1", 1);
    Assertions.assertEquals(1, result.length);
    Assertions.assertEquals(1L, result[0]);

  }

}

package test.mysql;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.ContainerUtil;
import test.container.JdbcContainer;

import javax.sql.DataSource;
import java.sql.Connection;

import static com.github.fantasy0v0.swift.jdbc.JDBC.*;

public class TransactionTest {

  private final Logger log = LoggerFactory.getLogger(TransactionTest.class);

  private final static JdbcContainer container = JdbcContainer.create(
    ContainerUtil.MYSQL, ContainerUtil.MYSQL_LOCATIONS
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
    transaction(() -> {
      select("select * from student").fetch();
      transaction(Connection.TRANSACTION_READ_UNCOMMITTED, () -> {
        select("select * from student").fetch();
        transaction(Connection.TRANSACTION_READ_COMMITTED, () -> {
          modify("update student set name = ? where id = ?")
            .execute("修改", 1L);
        });
      });
    });
  }

  @Test
  void rollback() {
    transaction(() -> {
      modify("update student set name = ? where id = ?")
        .execute("修改", 1L);
    });
  }

}

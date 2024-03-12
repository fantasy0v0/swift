package test;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

import static com.github.fantasy0v0.swift.jdbc.JDBC.*;

public class TransactionTest {

  private final Logger log = LoggerFactory.getLogger(TransactionTest.class);

  private static HikariDataSource dataSource;

  @BeforeAll
  static void beforeAll() throws SQLException {
    dataSource = DataSourceUtil.create();
    JDBC.configuration(dataSource);
  }

  @AfterAll
  static void afterAll() {
    if (null != dataSource) {
      dataSource.close();
    }
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

}

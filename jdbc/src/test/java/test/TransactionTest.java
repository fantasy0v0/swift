package test;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static com.github.fantasy0v0.swift.jdbc.JDBC.*;

public class TransactionTest {

  private final Logger log = LoggerFactory.getLogger(TransactionTest.class);

  @Test
  void test() throws SQLException {
    DataSource dataSource = DataSourceUtil.create();
    configuration(dataSource);
    transaction(() -> {
      select("select * from student").fetch();
      transaction(Connection.TRANSACTION_READ_UNCOMMITTED, () -> {
        modify("update student set name = ? where id = ?")
          .execute("修改", 1L);
      });
    });
  }

}

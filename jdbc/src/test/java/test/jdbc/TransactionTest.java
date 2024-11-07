package test.jdbc;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import com.github.fantasy0v0.swift.jdbc.exception.SwiftSQLException;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.ContainerUtil;
import test.container.JdbcTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static com.github.fantasy0v0.swift.jdbc.JDBC.*;

public class TransactionTest {

  private final Logger log = LoggerFactory.getLogger(TransactionTest.class);

  @TestFactory
  List<DynamicTest> testAllDatabase() {
    return ContainerUtil.testAllContainers(() -> List.of(
      new JdbcTest("test", this::test),
      new JdbcTest("rollback", this::rollback)
    ));
  }

  void test(DataSource dataSource) throws SQLException {
    String driverClassName = dataSource.unwrap(HikariDataSource.class).getDriverClassName();

    JDBC.configuration(dataSource);
    try {
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
    } catch (Exception e) {
      if (driverClassName.contains("postgresql")) {
        Assertions.assertEquals(SwiftSQLException.class, e.getClass());
      } else {
        Assertions.fail("错误的分支");
      }
      // MySQL虽然不会报错, 但是修改属于无效操作
      // 具体查看https://github.com/fantasy0v0/swift/issues/8#issuecomment-2461235425
    }
  }

  void rollback(DataSource dataSource) {
    JDBC.configuration(dataSource);
    transaction(() -> {
      modify("update student set name = ? where id = ?")
        .execute("修改", 1L);
    });
  }

}

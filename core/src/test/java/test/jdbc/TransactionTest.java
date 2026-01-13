package test.jdbc;

import com.github.fantasy0v0.swift.exception.SwiftSQLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.Db;
import test.container.SwiftJdbcExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static com.github.fantasy0v0.swift.Swift.*;

@ExtendWith(SwiftJdbcExtension.class)
public class TransactionTest {

  private final Logger log = LoggerFactory.getLogger(TransactionTest.class);

  @TestTemplate
  void test(DataSource dataSource, Db db) throws SQLException {
    try {
      transaction(() -> {
        select("select * from student").fetch();
        transaction(Connection.TRANSACTION_READ_UNCOMMITTED, () -> {
          select("select * from student").fetch();
          transaction(Connection.TRANSACTION_READ_COMMITTED, () -> {
            update("update student set name = ? where id = ?")
              .execute("修改", 1L);
          });
        });
      });
    } catch (Exception e) {
      if (Db.Postgres == db) {
        Assertions.assertEquals(SwiftSQLException.class, e.getClass());
      } else {
        Assertions.fail("错误的分支");
      }
      // MySQL虽然不会报错, 但是修改属于无效操作
      // 具体查看https://github.com/fantasy0v0/swift/issues/8#issuecomment-2461235425
    }
  }

  @TestTemplate
  void rollback() {
    long id = 1;
    String result = select("select name from student where id = ?", id)
      .fetchOne(row -> row.getString(1));
    Assertions.assertNotEquals("修改", result);
    try {
      transaction(() -> {
        update("update student set name = ? where id = ?")
          .execute("修改", id);
        String result1 = select("select name from student where id = ?", id)
          .fetchOne(row -> row.getString(1));
        Assertions.assertEquals("修改", result1);
        throw new RuntimeException("rollback");
      });
    } catch (RuntimeException e) {
      Assertions.assertEquals("rollback", e.getMessage());
    }
    result = select("select name from student where id = ?", id)
      .fetchOne(row -> row.getString(1));
    Assertions.assertNotEquals("修改", result);
  }

}

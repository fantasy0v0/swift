package test;

import com.github.fantasy0v0.swift.exception.SwiftSQLException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.Db;
import test.container.SwiftJdbcExtension;

import java.sql.Connection;

import static com.github.fantasy0v0.swift.Swift.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SwiftJdbcExtension.class)
public class TransactionTest {

  private final Logger log = LoggerFactory.getLogger(TransactionTest.class);

  @TestTemplate
  void test(Db db) {
    // 不能在事务交易过程中改变事物交易隔绝等级
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

    long testId = 1L;
    String oldName = select("select name from student where id = ?", testId)
      .fetchOne(row -> row.getString(1));
    log.debug("name: {}", oldName);
    transaction(() -> {
      update("update student set name = ? where id = ?")
        .execute("修改", 1L);
    });
    String name = select("select name from student where id = ?", testId)
      .fetchOne(row -> row.getString(1));
    assertEquals("修改", name);
  }

  @TestTemplate
  void rollback() {
    long testId = 1L;
    String oldName = select("select name from student where id = ?", testId)
      .fetchOne(row -> row.getString(1));
    log.debug("name: {}", oldName);
    try {
      transaction(() -> {
        update("update student set name = ? where id = ?")
          .execute("修改", 1L);
        String newName = select("select name from student where id = ?", testId)
          .fetchOne(row -> row.getString(1));
        assertEquals("修改", newName);
        throw new RuntimeException("rollback");
      });
    } catch (RuntimeException e) {
      assertEquals("rollback", e.getMessage());
    }
    String name = select("select name from student where id = ?", testId)
      .fetchOne(row -> row.getString(1));
    assertEquals(oldName, name);

    try {
      transaction(() -> {
        transaction(() -> {
          update("update student set name = ? where id = ?")
            .execute("修改", 1L);
        });
        String newName = select("select name from student where id = ?", testId)
          .fetchOne(row -> row.getString(1));
        assertEquals("修改", newName);
        throw new RuntimeException("rollback");
      });
    } catch (RuntimeException e) {
      assertEquals("rollback", e.getMessage());
    }
    name = select("select name from student where id = ?", testId)
      .fetchOne(row -> row.getString(1));
    assertEquals(oldName, name);
  }

}

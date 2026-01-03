package test;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.SwiftJdbcExtension;

import static com.github.fantasy0v0.swift.Swift.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SwiftJdbcExtension.class)
public class TransactionTest {

  private final Logger log = LoggerFactory.getLogger(TransactionTest.class);

  @TestTemplate
  void test() {
    // 不能在事务交易过程中改变事物交易隔绝等级
//    transaction(() -> {
//      select("select * from student").fetch();
//      transaction(Connection.TRANSACTION_READ_UNCOMMITTED, () -> {
//        select("select * from student").fetch();
//        transaction(Connection.TRANSACTION_READ_COMMITTED, () -> {
//          modify("update student set name = ? where id = ?")
//            .execute("修改", 1L);
//        });
//      });
//    });

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

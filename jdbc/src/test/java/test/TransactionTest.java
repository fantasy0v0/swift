package test;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.SwiftJdbcExtension;

import static com.github.fantasy0v0.swift.jdbc.JDBC.*;

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

    transaction(() -> {
      select("select * from student").fetch();
      transaction(() -> {
        select("select * from student").fetch();
        transaction(() -> {
          update("update student set name = ? where id = ?")
            .execute("修改", 1L);
        });
      });
    });
  }

  @TestTemplate
  void rollback() {
    transaction(() -> {
      update("update student set name = ? where id = ?")
        .execute("修改", 1L);
    });
  }

}

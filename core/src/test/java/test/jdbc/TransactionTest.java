package test.jdbc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.Db;
import test.container.SwiftJdbcExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@ExtendWith(SwiftJdbcExtension.class)
public class TransactionTest {

  private final Logger log = LoggerFactory.getLogger(TransactionTest.class);

  @TestTemplate
  void test(DataSource dataSource, Db db) throws SQLException {

  }

  @TestTemplate
  void rollback(DataSource dataSource) throws SQLException {
    final String selectSql = "select name from student where id = ?";
    final long id = 1;
    try (Connection connection = dataSource.getConnection()) {
      // 事前验证
      try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
        ps.setLong(1, id);
        try (ResultSet rs = ps.executeQuery()) {
          Assertions.assertTrue(rs.next());
          Assertions.assertNotEquals("修改", rs.getString(1));
        }
      }

      // 开启事务
      connection.setAutoCommit(false);
      try (PreparedStatement ps = connection.prepareStatement("update student set name = ? where id = ?")) {
        ps.setString(1, "修改");
        ps.setLong(2, id);
        ps.executeUpdate();
      }
      try (PreparedStatement ps = connection.prepareStatement("select name from student where id = ?")) {
        ps.setLong(1, id);
        try (ResultSet rs = ps.executeQuery()) {
          Assertions.assertTrue(rs.next());
          Assertions.assertEquals("修改", rs.getString(1));
        }
      }
      connection.rollback();
      connection.setAutoCommit(true);
      // 事务结束

      // 验证
      try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
        ps.setLong(1, id);
        try (ResultSet rs = ps.executeQuery()) {
          Assertions.assertTrue(rs.next());
          Assertions.assertNotEquals("修改", rs.getString(1));
        }
      }
    }
  }
}

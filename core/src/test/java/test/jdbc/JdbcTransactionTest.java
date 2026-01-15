package test.jdbc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.Db;
import test.container.SwiftJdbcExtension;

import javax.sql.DataSource;
import java.sql.*;

@ExtendWith(SwiftJdbcExtension.class)
public class JdbcTransactionTest {

  private final Logger log = LoggerFactory.getLogger(JdbcTransactionTest.class);

  @TestTemplate
  void test(DataSource dataSource, Db db) throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      // 这并不算是开启事务
      connection.setAutoCommit(false);
      // 事务开启之前可以任意修改隔离级别
      connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
      // 主动发起查询开启事务
      try (Statement stmt = connection.createStatement()) {
        try (ResultSet resultSet = stmt.executeQuery("select 1")) {
          Assertions.assertTrue(resultSet.next());
        }
      }
      int transactionIsolation = connection.getTransactionIsolation();
      log.debug("transactionIsolation: {}", transactionIsolation);
      // 开启事务后无法修改
      if (Db.Postgres == db) {
        Assertions.assertThrowsExactly(PSQLException.class, () -> {
          connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        });
      } else {
        // MySQL虽然不报错但是无法修改
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
      }
      connection.commit();
      log.debug("autoCommit: {}", connection.getAutoCommit());
    }
  }

  @TestTemplate
  void testSQLError(DataSource dataSource, Db db) throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      connection.setAutoCommit(false);
      try (Statement stmt = connection.createStatement()) {
        try (ResultSet resultSet = stmt.executeQuery("select 1")) {
          Assertions.assertTrue(resultSet.next());
        }
        // 主动制造错误
        try {
          try (ResultSet resultSet = stmt.executeQuery("select 1 / 0")) {
            Assertions.assertTrue(resultSet.next());
          }
        } catch (SQLException e) {
          log.debug("error", e);
        }

        // PostgreSQL在发生错误后, 无法再执行任何语句
        if (Db.Postgres == db) {
          Assertions.assertThrowsExactly(PSQLException.class, () -> {
            try (ResultSet resultSet = stmt.executeQuery("select 1")) {
              Assertions.assertTrue(resultSet.next());
            }
          });
        }
      }
      connection.commit();
      log.debug("autoCommit: {}", connection.getAutoCommit());
    }
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

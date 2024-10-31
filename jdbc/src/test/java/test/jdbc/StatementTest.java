package test.jdbc;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.ContainerUtil;
import test.container.JdbcContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StatementTest {

  private final static Logger log = LoggerFactory.getLogger(StatementTest.class);

  private final static JdbcContainer container = JdbcContainer.create(
    ContainerUtil.PG, ContainerUtil.PG_LOCATIONS
  );

  private static DataSource dataSource;

  @BeforeAll
  static void beforeAll() {
    dataSource = container.start();
  }

  @AfterAll
  static void afterAll() {
    container.stop();
  }

  @Test
  void execute() throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      String sql = "insert into swift_user(name, status) values(?, ?)";
      try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setQueryTimeout(1);
        ps.setString(1, "fantasy");
        ps.setInt(2, 1);
        assertEquals(false, ps.execute());
        try (ResultSet resultSet = ps.getGeneratedKeys()) {
          assertEquals(false, resultSet.next());
        }
      }
    }
  }

  @Test
  void executeUpdate() throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      String sql = "insert into swift_user(name, status) values(?, ?) returning id";
      try (PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setQueryTimeout(1);
        ps.setString(1, "fantasy");
        ps.setInt(2, 1);
        assertEquals(1, ps.executeUpdate());
        try (ResultSet resultSet = ps.getResultSet()) {
          assertTrue(resultSet.next());
          long id = resultSet.getLong(1);
          log.debug("id: {}", id);
          assertTrue(id > 0);
        }

        try (ResultSet resultSet = ps.getGeneratedKeys()) {
          assertTrue(resultSet.next());
          long id = resultSet.getLong(1);
          log.debug("key: {}", id);
          assertTrue(id > 0);
        }
      }
    }
  }

}

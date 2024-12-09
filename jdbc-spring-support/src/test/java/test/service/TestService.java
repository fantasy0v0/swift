package test.service;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import com.github.fantasy0v0.swift.jdbc.exception.SwiftException;
import jakarta.annotation.Resource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Service
public class TestService {

  @Resource
  private DataSource dataSource;

  @Transactional
  public void test(boolean throwError) throws SQLException {
    Connection connection = DataSourceUtils.doGetConnection(dataSource);
    try (PreparedStatement ps = connection.prepareStatement("update student set name = ? where id = ?")) {
      ps.setString(1, "大明");
      ps.setInt(2, 1);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } finally {
      DataSourceUtils.doReleaseConnection(connection, dataSource);
    }

//    JDBC.update("""
//    update student set name = ? where id = ?
//    """).execute("大明", 1);
    if (throwError) {
      throw new SwiftException("测试");
    }
  }

}

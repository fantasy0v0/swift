package test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DataSourceUtil {

  public static DataSource create() throws SQLException {
    HikariConfig config = new HikariConfig();
    config.setDriverClassName("org.h2.Driver");
    config.setJdbcUrl("jdbc:h2:mem:");
    HikariDataSource dataSource = new HikariDataSource(config);
    try (Connection connection = dataSource.getConnection()) {
      try (Statement statement = connection.createStatement()) {
        statement.execute("""
          CREATE TABLE student (
            id     INT NOT NULL,
            name   VARCHAR(50) NOT NULL,
            status INT NOT NULL,
            ext    VARCHAR(50)
          );
          insert into student(id, name, status) values
          (1, '小明', 0),
          (2, '张三', 1),
          (3, '李四', 2),
          (4, '董超', 2),
          (5, '薛霸', 2);
          """);
      }
    }
    return dataSource;
  }

}

package test.container;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.testcontainers.containers.JdbcDatabaseContainer;

import javax.sql.DataSource;

public class JdbcContainer {

  private final JdbcDatabaseContainer<?> container;

  private HikariDataSource dataSource;

  private JdbcContainer(JdbcDatabaseContainer<?> container) {
    this.container = container;
  }

  public static JdbcContainer create(JdbcDatabaseContainer<?> container) {
    return new JdbcContainer(container);
  }

  public DataSource start() {
    container.start();
    HikariConfig config = new HikariConfig();
    config.setDriverClassName(container.getDriverClassName());
    config.setJdbcUrl(container.getJdbcUrl());
    config.setUsername(container.getUsername());
    config.setPassword(container.getPassword());
    dataSource = new HikariDataSource(config);
    return dataSource;
  }

  public void stop() {
    if (null != dataSource) {
      dataSource.close();
    }
    container.stop();
  }

  public String getDockerImageName() {
    return container.getDockerImageName();
  }

}

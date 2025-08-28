package test.container;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.testcontainers.containers.JdbcDatabaseContainer;

import javax.sql.DataSource;

public class JdbcContainer {

  private final String locations;

  private final JdbcDatabaseContainer<?> container;

  private HikariDataSource dataSource;

  private JdbcContainer(JdbcDatabaseContainer<?> container, String locations) {
    this.locations = locations;
    this.container = container;
  }

  public static JdbcContainer create(JdbcDatabaseContainer<?> container, String locations) {
    return new JdbcContainer(container, locations);
  }

  public DataSource start() {
    container.start();
    HikariConfig config = new HikariConfig();
    config.setDriverClassName(container.getDriverClassName());
    config.setJdbcUrl(container.getJdbcUrl());
    config.setUsername(container.getUsername());
    config.setPassword(container.getPassword());
    dataSource = new HikariDataSource(config);
    Flyway flyway = Flyway.configure()
      .dataSource(dataSource)
      .locations(locations)
      .load();
    flyway.migrate();
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

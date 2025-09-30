package test.container;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

public final class ContainerUtil {

  public static final PostgreSQLContainer<?> PG = new PostgreSQLContainer<>("postgres:17-alpine")
    .withInitScript("db/pg/init.sql");

  public static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8")
    .withInitScript("db/mysql/init.sql");

  public static final List<JdbcContainer> containers = List.of(
    JdbcContainer.create(PG), JdbcContainer.create(MYSQL)
  );

}

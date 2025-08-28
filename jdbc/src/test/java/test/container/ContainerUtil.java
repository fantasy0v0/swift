package test.container;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;

public final class ContainerUtil {

  public static final PostgreSQLContainer<?> PG = new PostgreSQLContainer<>("postgres:17-alpine");

  public static final String PG_LOCATIONS = "classpath:db/pg";

  public static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8");

  public static final String MYSQL_LOCATIONS = "classpath:db/mysql";

  public static final List<JdbcContainer> containers = List.of(
    JdbcContainer.create(PG, PG_LOCATIONS),
    JdbcContainer.create(MYSQL, MYSQL_LOCATIONS)
  );

}

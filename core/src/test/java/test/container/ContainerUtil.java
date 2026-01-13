package test.container;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.TimeZone;

public final class ContainerUtil {

  private static final TimeZone timeZone = TimeZone.getDefault();

  public static final PostgreSQLContainer<?> PG = new PostgreSQLContainer<>("postgres:17-alpine")
    .withInitScript("db/pg/init.sql");

  public static final PostgreSQLContainer<?> PG_18 = new PostgreSQLContainer<>("postgres:18-alpine")
    .withInitScript("db/pg/init.sql");

  public static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8")
    .withUrlParam("serverTimezone", timeZone.getID()).withEnv("TZ", timeZone.getID())
    .withInitScript("db/mysql/init.sql");

  public static final List<JdbcContainer> containers = List.of(
    JdbcContainer.create(PG), JdbcContainer.create(PG_18),
    JdbcContainer.create(MYSQL)
  );

}

package test.container;

import org.junit.jupiter.api.DynamicTest;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public final class ContainerUtil {

  public static final PostgreSQLContainer<?> PG = new PostgreSQLContainer<>("postgres:16-alpine");

  public static final String PG_LOCATIONS = "classpath:db/pg";

  public static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8");

  public static final String MYSQL_LOCATIONS = "classpath:db/mysql";

  public static final List<JdbcContainer> containers = List.of(
    JdbcContainer.create(PG, PG_LOCATIONS),
    JdbcContainer.create(MYSQL, MYSQL_LOCATIONS)
  );

  public static List<DynamicTest> testAllContainers(Executable executable) {
    List<DynamicTest> tests = new ArrayList<>();
    for (JdbcContainer container : ContainerUtil.containers) {
      DataSource dataSource = container.start();
      String name = container.getDriverClassName();
      tests.addAll(
        executable.execute(dataSource)
          .stream().map(test -> dynamicTest(name + " " + test.name(),test.executable()))
          .toList()
      );
    }
    return tests;
  }

}

package test.container;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import org.junit.jupiter.api.DynamicTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public final class ContainerUtil {

  private static final Logger log = LoggerFactory.getLogger(ContainerUtil.class);

  public static final PostgreSQLContainer<?> PG = new PostgreSQLContainer<>("postgres:16-alpine");

  public static final String PG_LOCATIONS = "classpath:db/pg";

  public static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8");

  public static final String MYSQL_LOCATIONS = "classpath:db/mysql";

  public static final List<JdbcContainer> containers = List.of(
    JdbcContainer.create(PG, PG_LOCATIONS),
    JdbcContainer.create(MYSQL, MYSQL_LOCATIONS)
  );

  public static List<DynamicTest> testAllContainers(ContainerExecutable executable) {
    List<DynamicTest> tests = new ArrayList<>();
    for (JdbcContainer container : ContainerUtil.containers) {
      String name = container.getDriverClassName();
      tests.addAll(
        executable.execute()
          .stream()
          .map(test -> dynamicTest(name + " " + test.name(), () -> {
            DataSource dataSource = container.start();
            JDBC.configuration(dataSource);
            try {
              test.executable().execute(dataSource);
            } finally {
              container.stop();
            }
          }))
          .toList()
      );
    }
    return tests;
  }

}

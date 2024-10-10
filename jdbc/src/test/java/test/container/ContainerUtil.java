package test.container;

import org.testcontainers.containers.PostgreSQLContainer;

public final class ContainerUtil {

  public static final PostgreSQLContainer<?> PG = new PostgreSQLContainer<>("postgres:16-alpine");

  public static final String PG_LOCATIONS = "classpath:db/pg";

}

package test;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.SwiftJdbcExtension;

import javax.sql.DataSource;
import java.sql.Array;
import java.sql.SQLException;
import java.util.List;

import static com.github.fantasy0v0.swift.jdbc.JDBC.select;

/**
 * @author fan 2025/8/28
 */
@ExtendWith(SwiftJdbcExtension.class)
class RowTest {

  private final Logger log = LoggerFactory.getLogger(RowTest.class);

  @TestTemplate
  void testArray(DataSource dataSource) throws SQLException {
    String driverClassName = dataSource.unwrap(HikariDataSource.class).getDriverClassName();
    if (!driverClassName.contains("postgresql")) {
      log.info("仅支持PostgreSQL: {}", driverClassName);
      return;
    }
    List<Array> arrays = select("select tags from swift_user").fetch(row -> row.getArray(1));
    for (Array array : arrays) {
      log.debug("{}, BaseType: {} BaseTypeName: {} getArray: {}", array, array.getBaseType(), array.getBaseTypeName(), array.getArray());
    }
  }

}

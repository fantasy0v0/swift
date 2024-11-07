package test.container;

import javax.sql.DataSource;
import java.sql.SQLException;

@FunctionalInterface
public interface JdbcExecutable {

  void execute(DataSource dataSource) throws SQLException;

}

package test.container;

import javax.sql.DataSource;
import java.util.List;

@FunctionalInterface
public interface Executable {

  List<JdbcTest> execute(DataSource dataSource);

}

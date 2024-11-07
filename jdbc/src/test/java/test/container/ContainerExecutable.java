package test.container;

import java.util.List;

@FunctionalInterface
public interface ContainerExecutable {

  List<JdbcTest> execute();

}

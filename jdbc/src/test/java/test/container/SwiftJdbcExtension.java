package test.container;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import org.junit.jupiter.api.extension.*;

import javax.sql.DataSource;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class SwiftJdbcExtension implements TestTemplateInvocationContextProvider {

  @Override
  public boolean supportsTestTemplate(ExtensionContext extensionContext) {
    return true;
  }

  @Override
  public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext extensionContext) {
    Db[] dbs = extensionContext.getTestMethod()
      .map(m -> m.getAnnotation(Allowed.class))
      .map(Allowed::value)
      .orElse(new Db[0]);
    if (0 == dbs.length) {
      return ContainerUtil.containers.stream().map(this::invocationContext);
    }
    return ContainerUtil.containers.stream()
      .filter(container -> contains(dbs, container.getDockerImageName()))
      .map(this::invocationContext);
  }

  private boolean contains(Db[] dbs, String name) {
    for (Db db : dbs) {
      if (name.contains(db.name)) {
        return true;
      }
    }
    return false;
  }

  private TestTemplateInvocationContext invocationContext(JdbcContainer container) {
    return new TestTemplateInvocationContext() {
      @Override
      public String getDisplayName(int invocationIndex) {
        return container.getDockerImageName();
      }

      @Override
      public List<Extension> getAdditionalExtensions() {
        return Collections.singletonList(new InnerExtension(container));
      }
    };
  }

  static class InnerExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver {

    private final JdbcContainer container;

    private DataSource dataSource;

    InnerExtension(JdbcContainer container) {
      this.container = container;
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
      JDBC.setContext(null);
      container.stop();
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) {
      dataSource = container.start();
      JDBC.setContext(JDBC.newContext(dataSource));
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
      Parameter parameter = parameterContext.getParameter();
      if (parameter.getType().equals(DataSource.class)) {
        return true;
      }
      return parameter.getType().equals(Db.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
      Parameter parameter = parameterContext.getParameter();
      if (parameter.getType().equals(DataSource.class)) {
        return dataSource;
      }
      String dockerImageName = container.getDockerImageName();
      if (dockerImageName.contains(Db.Postgres.name)) {
        return Db.Postgres;
      } else if (dockerImageName.contains(Db.MySQL.name)) {
        return Db.MySQL;
      }
      return null;
    }
  }
}

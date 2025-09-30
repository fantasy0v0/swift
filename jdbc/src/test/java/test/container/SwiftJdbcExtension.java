package test.container;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import org.junit.jupiter.api.extension.*;

import javax.sql.DataSource;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SwiftJdbcExtension implements TestTemplateInvocationContextProvider,
  BeforeAllCallback {

  private final static Map<JdbcContainer, DataSource> containerMap = new HashMap<>();

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
      return containerMap.entrySet().stream().map(this::invocationContext);
    }
    return containerMap.entrySet().stream()
      .filter(entry -> contains(dbs, entry.getKey().getDockerImageName()))
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

  private TestTemplateInvocationContext invocationContext(Map.Entry<JdbcContainer, DataSource> entry) {
    final String dockerImageName = entry.getKey().getDockerImageName();
    final DataSource dataSource = entry.getValue();
    return new TestTemplateInvocationContext() {
      @Override
      public String getDisplayName(int invocationIndex) {
        return dockerImageName;
      }

      @Override
      public List<Extension> getAdditionalExtensions() {
        return Collections.singletonList(new InnerExtension(dockerImageName, dataSource));
      }
    };
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    if (containerMap.isEmpty()) {
      for (JdbcContainer container : ContainerUtil.containers) {
        containerMap.put(container, container.start());
      }
    }
    ExtensionContext.Store store = context.getRoot().getStore(ExtensionContext.Namespace.GLOBAL);
    store.getOrComputeIfAbsent("GLOBAL_RESOURCE", key -> (ExtensionContext.Store.CloseableResource) () -> {
      for (JdbcContainer container : containerMap.keySet()) {
        container.stop();
      }
      containerMap.clear();
    });
  }

  static class InnerExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback, ParameterResolver {

    private final String dockerImageName;

    private final DataSource dataSource;

    InnerExtension(String dockerImageName, DataSource dataSource) {
      this.dockerImageName = dockerImageName;
      this.dataSource = dataSource;
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
      JDBC.setContext(null);
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) {
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
      if (dockerImageName.contains(Db.Postgres.name)) {
        return Db.Postgres;
      } else if (dockerImageName.contains(Db.MySQL.name)) {
        return Db.MySQL;
      }
      return null;
    }
  }
}

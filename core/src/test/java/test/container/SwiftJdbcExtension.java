package test.container;

import com.github.fantasy0v0.swift.ConnectionPoolUtil;
import com.github.fantasy0v0.swift.Context;
import com.github.fantasy0v0.swift.Swift;
import com.github.fantasy0v0.swift.connection.ConnectionReference;
import com.github.fantasy0v0.swift.connection.ConnectionTransaction;
import org.junit.jupiter.api.extension.*;

import javax.sql.DataSource;
import java.lang.reflect.Parameter;
import java.sql.SQLException;
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

    private static final ExtensionContext.Namespace TEST_CONTEXT_NAMESPACE =
      ExtensionContext.Namespace.create(SwiftJdbcExtension.class);

    private static final String ConnectionReferenceKey = "ConnectionReference";

    private static final String ConnectionTransactionKey = "ConnectionTransaction";

    private final String dockerImageName;

    private final DataSource dataSource;

    private Context context;

    InnerExtension(String dockerImageName, DataSource dataSource) {
      this.dockerImageName = dockerImageName;
      this.dataSource = dataSource;
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
      return context.getRoot().getStore(TEST_CONTEXT_NAMESPACE);
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws SQLException {
      context = Swift.newContext(dataSource);
      Swift.setContext(context);
      ConnectionReference reference = ConnectionPoolUtil.getReference(context);
      getStore(extensionContext).put(ConnectionReferenceKey, reference);
      ConnectionTransaction transaction = reference.getTransaction(null);
      getStore(extensionContext).put(ConnectionTransactionKey, transaction);
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
      ConnectionTransaction transaction = (ConnectionTransaction) getStore(extensionContext).get(ConnectionTransactionKey);
      transaction.rollback();
      ConnectionReference reference = (ConnectionReference) getStore(extensionContext).get(ConnectionReferenceKey);
      reference.close();
      Swift.setContext(null);
      context = null;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
      Parameter parameter = parameterContext.getParameter();
      if (parameter.getType().equals(DataSource.class)) {
        return true;
      } else if (parameter.getType().equals(Db.class)) {
        return true;
      }
      return parameter.getType().equals(Context.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
      Parameter parameter = parameterContext.getParameter();
      if (parameter.getType().equals(DataSource.class)) {
        return dataSource;
      } else if (parameter.getType().equals(Db.class)) {
        if (dockerImageName.contains(Db.Postgres.name)) {
          return Db.Postgres;
        } else if (dockerImageName.contains(Db.MySQL.name)) {
          return Db.MySQL;
        }
      } else if (parameter.getType().equals(Context.class)) {
        return context;
      }
      return null;
    }
  }
}

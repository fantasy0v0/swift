package test.container;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import org.junit.jupiter.api.extension.*;

import javax.sql.DataSource;
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
    return ContainerUtil.containers.stream().map(this::invocationContext);
  }

  private TestTemplateInvocationContext invocationContext(JdbcContainer container) {
    return new TestTemplateInvocationContext() {
      @Override
      public String getDisplayName(int invocationIndex) {
        return container.getDriverClassName();
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
      container.stop();
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
      dataSource = container.start();
      JDBC.configuration(dataSource);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
      return parameterContext.getParameter().getType().equals(DataSource.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
      return dataSource;
    }
  }
}

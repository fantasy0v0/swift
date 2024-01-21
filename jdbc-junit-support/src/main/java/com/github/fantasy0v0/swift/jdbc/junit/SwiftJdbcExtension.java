package com.github.fantasy0v0.swift.jdbc.junit;

import com.github.fantasy0v0.swift.jdbc.ConnectionReference;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Connection;

public class SwiftJdbcExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

  private static final ExtensionContext.Namespace TEST_CONTEXT_MANAGER_NAMESPACE =
    ExtensionContext.Namespace.create(SpringExtension.class);

  private ConnectionReference ref;

  @Override
  public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
    if (null != ref) {
      Connection connection = ref.unwrap();
      connection.rollback();
    }
  }

  @Override
  public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
    ApplicationContext applicationContext = getTestContextManager(extensionContext)
      .getTestContext().getApplicationContext();
    DataSource dataSource = applicationContext.getBean(DataSource.class);
    ConnectionReference ref = ConnectionReference.getReference(dataSource);
    Connection connection = ref.unwrap();
    connection.setAutoCommit(false);
    this.ref = ref;
  }

  static TestContextManager getTestContextManager(ExtensionContext context) {
    Assert.notNull(context, "ExtensionContext must not be null");
    Class<?> testClass = context.getRequiredTestClass();
    ExtensionContext.Store store = getStore(context);
    return store.getOrComputeIfAbsent(testClass, TestContextManager::new, TestContextManager.class);
  }

  private static ExtensionContext.Store getStore(ExtensionContext context) {
    return context.getRoot().getStore(TEST_CONTEXT_MANAGER_NAMESPACE);
  }
}

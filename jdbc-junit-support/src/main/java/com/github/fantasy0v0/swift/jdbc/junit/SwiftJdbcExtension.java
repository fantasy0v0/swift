package com.github.fantasy0v0.swift.jdbc.junit;

import com.github.fantasy0v0.swift.jdbc.ConnectionPoolUtil;
import com.github.fantasy0v0.swift.jdbc.ConnectionReference;
import com.github.fantasy0v0.swift.jdbc.JDBC;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Objects;

public class SwiftJdbcExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

  private static final ExtensionContext.Namespace TEST_CONTEXT_NAMESPACE =
    ExtensionContext.Namespace.create(SwiftJdbcExtension.class);

  @Override
  public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
    ConnectionReference ref = getConnectionReference(extensionContext);
    if (null != ref) {
      Connection connection = ref.unwrap();
      connection.rollback();
      ref.close();
    }
  }

  @Override
  public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
    DataSource dataSource = Objects.requireNonNull(JDBC.getDataSource(), "未配置dataSource");
    ConnectionReference ref = ConnectionPoolUtil.getReference(dataSource);
    Connection connection = ref.unwrap();
    connection.setAutoCommit(false);
    setConnectionReference(extensionContext, ref);
  }

  void setConnectionReference(ExtensionContext context,
                              ConnectionReference connectionReference) {
    ExtensionContext.Store store = getStore(context);
    store.put("connectionReference", connectionReference);
  }

  ConnectionReference getConnectionReference(ExtensionContext context) {
    ExtensionContext.Store store = getStore(context);
    return (ConnectionReference) store.get("connectionReference");
  }

  private ExtensionContext.Store getStore(ExtensionContext context) {
    return context.getRoot().getStore(TEST_CONTEXT_NAMESPACE);
  }
}

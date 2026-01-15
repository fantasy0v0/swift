package com.github.fantasy0v0.swift.junit;

import com.github.fantasy0v0.swift.ConnectionPoolUtil;
import com.github.fantasy0v0.swift.Swift;
import com.github.fantasy0v0.swift.connection.ManagedConnection;
import com.github.fantasy0v0.swift.connection.ManagedTransaction;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class SwiftJdbcExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

  private static final ExtensionContext.Namespace TEST_CONTEXT_NAMESPACE =
    ExtensionContext.Namespace.create(SwiftJdbcExtension.class);

  @Override
  public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
    ManagedTransaction transaction = getTransaction(extensionContext);
    if (null != transaction) {
      transaction.rollback();
    }
    ManagedConnection conn = getConnection(extensionContext);
    if (null != conn) {
      conn.close();
    }
  }

  @Override
  public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
    ManagedConnection ref = ConnectionPoolUtil.getConnection(Swift.getContext());
    setConnection(extensionContext, ref);
    ManagedTransaction transaction = ref.getTransaction(null);
    setTransaction(extensionContext, transaction);
  }

  void setConnection(ExtensionContext context,
                     ManagedConnection managedConnection) {
    ExtensionContext.Store store = getStore(context);
    store.put("ManagedConnection", managedConnection);
  }

  ManagedConnection getConnection(ExtensionContext context) {
    ExtensionContext.Store store = getStore(context);
    return (ManagedConnection) store.get("ManagedConnection");
  }

  void setTransaction(ExtensionContext context,
                      ManagedTransaction transaction) {
    ExtensionContext.Store store = getStore(context);
    store.put("Transaction", transaction);
  }

  ManagedTransaction getTransaction(ExtensionContext context) {
    ExtensionContext.Store store = getStore(context);
    return (ManagedTransaction) store.get("Transaction");
  }

  private ExtensionContext.Store getStore(ExtensionContext context) {
    return context.getRoot().getStore(TEST_CONTEXT_NAMESPACE);
  }
}

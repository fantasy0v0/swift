package com.github.fantasy0v0.swift.junit;

import com.github.fantasy0v0.swift.ConnectionPoolUtil;
import com.github.fantasy0v0.swift.Swift;
import com.github.fantasy0v0.swift.connection.ConnectionReference;
import com.github.fantasy0v0.swift.connection.ConnectionTransaction;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class SwiftJdbcExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

  private static final ExtensionContext.Namespace TEST_CONTEXT_NAMESPACE =
    ExtensionContext.Namespace.create(SwiftJdbcExtension.class);

  @Override
  public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
    ConnectionTransaction transaction = getConnectionTransaction(extensionContext);
    if (null != transaction) {
      transaction.rollback();
    }
    ConnectionReference ref = getConnectionReference(extensionContext);
    if (null != ref) {
      ref.close();
    }
  }

  @Override
  public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
    ConnectionReference ref = ConnectionPoolUtil.getReference(Swift.getContext());
    setConnectionReference(extensionContext, ref);
    ConnectionTransaction transaction = ref.getTransaction(null);
    setConnectionTransaction(extensionContext, transaction);
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

  void setConnectionTransaction(ExtensionContext context,
                                ConnectionTransaction transaction) {
    ExtensionContext.Store store = getStore(context);
    store.put("ConnectionTransaction", transaction);
  }

  ConnectionTransaction getConnectionTransaction(ExtensionContext context) {
    ExtensionContext.Store store = getStore(context);
    return (ConnectionTransaction) store.get("ConnectionTransaction");
  }

  private ExtensionContext.Store getStore(ExtensionContext context) {
    return context.getRoot().getStore(TEST_CONTEXT_NAMESPACE);
  }
}

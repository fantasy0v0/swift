package com.github.fantasy0v0.swift.jdbc.junit;

import com.github.fantasy0v0.swift.jdbc.ConnectionReference;
import com.github.fantasy0v0.swift.jdbc.JDBC;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Objects;

public class SwiftJdbcExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

  @Override
  public void afterTestExecution(ExtensionContext extensionContext) throws Exception {
    DataSource dataSource = Objects.requireNonNull(JDBC.getDataSource(), "未配置dataSource");
    ConnectionReference ref = ConnectionReference.getReference(dataSource);
    if (null != ref) {
      Connection connection = ref.unwrap();
      connection.rollback();
      ref.close();
    }
  }

  @Override
  public void beforeTestExecution(ExtensionContext extensionContext) throws Exception {
    DataSource dataSource = Objects.requireNonNull(JDBC.getDataSource(), "未配置dataSource");
    ConnectionReference ref = ConnectionReference.getReference(dataSource);
    Connection connection = ref.unwrap();
    connection.setAutoCommit(false);
  }
}

package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.dialect.SQLDialect;
import com.github.fantasy0v0.swift.jdbc.exception.SwiftSQLException;
import com.github.fantasy0v0.swift.jdbc.parameter.ParameterGetter;
import com.github.fantasy0v0.swift.jdbc.parameter.ParameterSetter;
import com.github.fantasy0v0.swift.jdbc.type.AbstractTypeHandler;
import com.github.fantasy0v0.swift.jdbc.type.TypeGetHandler;
import com.github.fantasy0v0.swift.jdbc.type.TypeSetHandler;
import com.github.fantasy0v0.swift.jdbc.util.LogUtil;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 非线程安全 no thread safe
 */
public class Context {

  private final DataSource dataSource;

  private final SQLDialect dialect;

  private final Map<Class<?>, TypeGetHandler<?>> getHandlerMap = new ConcurrentHashMap<>();

  private final Map<Class<?>, TypeSetHandler<?>> setHandlerMap = new ConcurrentHashMap<>();

  private final Map<Class<?>, ParameterGetter<?>> getterMap = new HashMap<>();

  private final Map<Class<?>, ParameterSetter<?>> setterMap = new HashMap<>();

  private StatementConfiguration statementConfiguration;

  public Context(DataSource dataSource, SQLDialect dialect, StatementConfiguration statementConfiguration) {
    this.dataSource = dataSource;
    dialect.install(dataSource);
    this.dialect = dialect;
    this.statementConfiguration = statementConfiguration;
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public SQLDialect getSQLDialect() {
    return dialect;
  }

  public <T> void configure(AbstractTypeHandler<T> typeHandler) {
    configure((TypeGetHandler<T>)typeHandler);
    configure((TypeSetHandler<T>)typeHandler);
  }

  public <T> void configure(TypeGetHandler<T> handler) {
    Class<T> supported = handler.support();
    if (getHandlerMap.containsKey(supported)) {
      LogUtil.common().debug("{} 原有的GetHandler将被替换", supported);
    }
    getHandlerMap.put(supported, handler);
  }

  public <T> void configure(TypeSetHandler<T> handler) {
    Class<T> supported = handler.support();
    if (setHandlerMap.containsKey(supported)) {
      LogUtil.common().debug("{} 原有的SetHandler将被替换", supported);
    }
    setHandlerMap.put(supported, handler);
  }

  public void configure(StatementConfiguration statementConfiguration) {
    this.statementConfiguration = statementConfiguration;
    LogUtil.common().debug("配置Statement Configuration");
  }

  public StatementConfiguration getStatementConfiguration() {
    return statementConfiguration;
  }

  public Map<Class<?>, TypeGetHandler<?>> getGetHandlers() {
    return getHandlerMap;
  }

  public Map<Class<?>, TypeSetHandler<?>> getSetHandlers() {
    return setHandlerMap;
  }

  public SelectBuilder select(String sql, List<Object> params) {
    return new SelectBuilder(this, sql.trim(), params);
  }

  public SelectBuilder select(String sql, Object... params) {
    return select(sql, Arrays.stream(params).toList());
  }

  public InsertBuilder insert(String sql) {
    return new InsertBuilder(this, sql.trim());
  }

  public UpdateBuilder update(String sql) {
    return new UpdateBuilder(this, sql.trim());
  }

  public void transaction(Integer level, Runnable runnable) {
    TransactionBuilder<?> builder = TransactionBuilder.create(this, level, runnable);
    try {
      builder.execute();
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  public void transaction(Runnable runnable) {
    transaction(null, runnable);
  }

  public <T> T transaction(Integer level, Supplier<T> supplier) {
    TransactionBuilder<T> builder = TransactionBuilder.create(this, level, supplier);
    try {
      return builder.execute();
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  public <T> T transaction(Supplier<T> supplier) {
    return transaction(null, supplier);
  }
}

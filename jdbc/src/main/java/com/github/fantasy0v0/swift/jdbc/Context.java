package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.dialect.ANSI;
import com.github.fantasy0v0.swift.jdbc.dialect.SQLDialect;
import com.github.fantasy0v0.swift.jdbc.type.AbstractTypeHandler;
import com.github.fantasy0v0.swift.jdbc.type.TypeGetHandler;
import com.github.fantasy0v0.swift.jdbc.type.TypeSetHandler;
import com.github.fantasy0v0.swift.jdbc.util.LogUtil;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Context {

  private DataSource dataSource;

  private SQLDialect dialect;

  private final Map<Class<?>, TypeGetHandler<?>> getHandlerMap = new ConcurrentHashMap<>();

  private final Map<Class<?>, TypeSetHandler<?>> setHandlerMap = new ConcurrentHashMap<>();

  private StatementConfiguration statementConfiguration;

  public Context(DataSource dataSource, SQLDialect dialect) {
    this.dataSource = dataSource;
    configuration(dialect);
  }

  public void configuration(DataSource dataSource) {
    this.dataSource = dataSource;
    if (null != dialect) {
      configuration(this.dialect);
    }
    LogUtil.common().debug("更新数据源");
  }

  public void configuration(SQLDialect dialect) {
    dialect.install(dataSource);
    this.dialect = dialect;
    LogUtil.common().debug("配置方言");
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public SQLDialect getSQLDialect() {
    return null != dialect ? dialect : ANSI.Instance;
  }

  public <T> void configuration(AbstractTypeHandler<T> typeHandler) {
    configuration((TypeGetHandler<T>)typeHandler);
    configuration((TypeSetHandler<T>)typeHandler);
  }

  public <T> void configuration(TypeGetHandler<T> handler) {
    Class<T> supported = handler.support();
    if (getHandlerMap.containsKey(supported)) {
      LogUtil.common().debug("{} 原有的GetHandler将被替换", supported);
    }
    getHandlerMap.put(supported, handler);
  }

  public <T> void configuration(TypeSetHandler<T> handler) {
    Class<T> supported = handler.support();
    if (setHandlerMap.containsKey(supported)) {
      LogUtil.common().debug("{} 原有的SetHandler将被替换", supported);
    }
    setHandlerMap.put(supported, handler);
  }

  public void configuration(StatementConfiguration statementConfiguration) {
    this.statementConfiguration = statementConfiguration;
    LogUtil.common().debug("配置Statement Configuration");
  }

  public StatementConfiguration getStatementConfiguration() {
    if (null == statementConfiguration) {
      statementConfiguration = new StatementConfiguration();
    }
    return statementConfiguration;
  }

  public SelectBuilder select(String sql, List<Object> params) {
    return new SelectBuilder(this, sql.trim(), params);
  }

  public SelectBuilder select(String sql, Object... params) {
    return select(sql, Arrays.stream(params).toList());
  }
}

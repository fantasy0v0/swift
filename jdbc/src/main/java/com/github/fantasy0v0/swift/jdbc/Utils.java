package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.connection.ConnectionReference;
import com.github.fantasy0v0.swift.jdbc.exception.SwiftException;
import com.github.fantasy0v0.swift.jdbc.type.TypeGetHandler;
import com.github.fantasy0v0.swift.jdbc.type.TypeSetHandler;
import com.github.fantasy0v0.swift.jdbc.util.LogUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class Utils {

  static Object[] fetchByRow(Row row) throws SQLException {
    int columnCount = row.getColumnCount();
    Object[] array = new Object[columnCount];
    for (int i = 0; i < columnCount; i++) {
      array[i] = row.getObject(i + 1);
    }
    return array;
  }

  static <T> List<T> fetch(Context context,
                           StatementConfiguration statementConfiguration,
                           String sql, List<Object> params,
                           FetchMapper<T> mapper, ParameterHandler parameterHandler) throws SQLException {

    try (ConnectionReference ref = ConnectionPoolUtil.getReference(context)) {
      return executeQuery(context,
        ref.unwrap(), statementConfiguration, context.getGetHandlers(), sql, params,
        mapper, parameterHandler, false
      );
    }
  }

  static <T> T fetchOne(Context context,
                        StatementConfiguration statementConfiguration,
                        String sql, List<Object> params,
                        FetchMapper<T> mapper, ParameterHandler parameterHandler) throws SQLException {
    try (ConnectionReference ref = ConnectionPoolUtil.getReference(context)) {
      List<T> list = executeQuery(
        context, ref.unwrap(), statementConfiguration, context.getGetHandlers(),
        sql, params, mapper, parameterHandler, true
      );
      return list.isEmpty() ? null : list.getFirst();
    }
  }

  static <T> List<T> fetchByResultSet(ResultSet resultSet,
                                      Map<Class<?>, TypeGetHandler<?>> handlerMap,
                                      FetchMapper<T> fetchMapper,
                                      boolean firstOnly) throws SQLException {
    List<T> array = new ArrayList<>();
    boolean first = true;
    while (resultSet.next()) {
      T row = fetchMapper.apply(new Row(resultSet, handlerMap));
      array.add(row);
      if (first && firstOnly) {
        // isLast不一定所有jdbc驱动都支持, 所以这里只能用next
        if (resultSet.next()) {
          throw new SwiftException("Expected one result, but found more than one");
        }
        break;
      }
      first = false;
    }
    return array;
  }

  /**
   * @return 打印调用者信息, 方便定位
   */
  static String printCallerInfo() {
    if (!LogUtil.sql().isDebugEnabled()) {
      return "need to enable the debug log level";
    }
    var optional = StackWalker.getInstance().walk(s ->
      s.skip(1)
        .filter(f -> !f.getClassName().startsWith("com.github.fantasy0v0.swift.jdbc"))
        .findFirst()
    );
    if (optional.isEmpty()) {
      return "not found";
    }
    var stackFrame = optional.get();
    String className = stackFrame.getClassName();
    String fileName = stackFrame.getFileName();
    int lineNumber = stackFrame.getLineNumber();
    return "%s(%s:%d)".formatted(className, fileName, lineNumber);
  }

  /**
   * 执行查询语句
   * @param conn 数据库连接
   * @param sql sql语句
   * @param params 变量
   * @param fetchMapper 行映射
   * @param parameterHandler 参数处理器
   * @param firstOnly 只取一条
   * @return 结果
   * @param <T> 映射后的类型
   * @throws SQLException 执行失败异常
   */
  static <T> List<T> executeQuery(Context context, Connection conn,
                                  StatementConfiguration statementConfiguration,
                                  Map<Class<?>, TypeGetHandler<?>> handlerMap,
                                  String sql, List<Object> params,
                                  FetchMapper<T> fetchMapper,
                                  ParameterHandler parameterHandler,
                                  boolean firstOnly) throws SQLException {
    StopWatch stopWatch = new StopWatch();
    LogUtil.performance().trace("executeQuery begin");
    String callerInfo = printCallerInfo();
    LogUtil.sql().debug("executeQuery: [{}], caller: {}", sql, callerInfo);
    try (PreparedStatement statement = prepareStatement(conn, sql, statementConfiguration)) {
      fillStatementParams(context, conn, statement, params, parameterHandler);
      try (ResultSet resultSet = statement.executeQuery()) {
        return fetchByResultSet(resultSet, handlerMap, fetchMapper, firstOnly);
      }
    } finally {
      LogUtil.performance().debug("executeQuery cost: {}", stopWatch);
    }
  }

  static <T> List<T> execute(Context context, Connection conn,
                             StatementConfiguration statementConfiguration,
                             String sql, List<Object> params,
                             ParameterHandler parameterHandler,
                             FetchMapper<T> mapper, boolean firstOnly) throws SQLException {
    StopWatch stopWatch = new StopWatch();
    LogUtil.performance().trace("execute begin");
    String callerInfo = printCallerInfo();
    LogUtil.sql().debug("execute: [{}], caller: {}", sql, callerInfo);
    try (PreparedStatement statement = prepareStatement(conn, sql, statementConfiguration)) {
      fillStatementParams(context, conn, statement, params, parameterHandler);
      boolean result = statement.execute();
      if (!result) {
        return null;
      }
      try (ResultSet resultSet = statement.getResultSet()) {
        return fetchByResultSet(resultSet, context.getGetHandlers(), mapper, firstOnly);
      }
    } finally {
      LogUtil.performance().debug("execute cost: {}", stopWatch);
    }
  }

  static <T> List<T> executeBatch(Context context, Connection conn,
                                  StatementConfiguration statementConfiguration,
                                  String sql, List<List<Object>> batch,
                                  ParameterHandler parameterHandler,
                                  FetchMapper<T> mapper) throws SQLException {
    StopWatch stopWatch = new StopWatch();
    LogUtil.performance().trace("executeBatch RETURN_GENERATED_KEYS begin");
    String callerInfo = printCallerInfo();
    LogUtil.sql().debug("executeBatch RETURN_GENERATED_KEYS: [{}], caller: {}", sql, callerInfo);
    try (PreparedStatement statement = prepareStatement(conn, sql,
      Statement.RETURN_GENERATED_KEYS, statementConfiguration)
    ) {
      for (List<Object> params : batch) {
        fillStatementParams(context, conn, statement, params, parameterHandler);
        statement.addBatch();
      }
      int[] result = statement.executeBatch();
      LogUtil.sql().debug("executeBatch: {}", result.length);
      return fetchByResultSet(
        statement.getGeneratedKeys(), context.getGetHandlers(), mapper, false
      );
    } finally {
      LogUtil.performance().debug("executeBatch RETURN_GENERATED_KEYS cost: {}", stopWatch);
    }
  }

  static int[] executeBatch(Context context, Connection conn,
                            StatementConfiguration statementConfiguration,
                            String sql, List<List<Object>> batch,
                            ParameterHandler parameterHandler) throws SQLException {
    StopWatch stopWatch = new StopWatch();
    LogUtil.performance().trace("executeBatch begin");
    String callerInfo = printCallerInfo();
    LogUtil.sql().debug("executeBatch: [{}], caller: {}", sql, callerInfo);
    try (PreparedStatement statement = prepareStatement(conn, sql,
      Statement.NO_GENERATED_KEYS, statementConfiguration)
    ) {
      for (List<Object> params : batch) {
        fillStatementParams(context, conn, statement, params, parameterHandler);
        statement.addBatch();
      }
      return statement.executeBatch();
    } finally {
      LogUtil.performance().debug("executeBatch cost: {}", stopWatch);
    }
  }

  static int executeUpdate(Context context, Connection conn,
                           StatementConfiguration statementConfiguration,
                           String sql, List<Object> params,
                           ParameterHandler parameterHandler) throws SQLException {
    StopWatch stopWatch = new StopWatch();
    LogUtil.performance().trace("executeUpdate begin");
    String callerInfo = printCallerInfo();
    LogUtil.sql().debug("executeUpdate: [{}], caller: {}", sql, callerInfo);
    try (PreparedStatement statement = prepareStatement(conn, sql, statementConfiguration)) {
      fillStatementParams(context, conn, statement, params, parameterHandler);
      return statement.executeUpdate();
    } finally {
      LogUtil.performance().debug("executeUpdate cost: {}", stopWatch);
    }
  }

  @SuppressWarnings("unchecked")
  static void fillStatementParams(Context context, Connection conn,
                                  PreparedStatement statement, List<Object> params,
                                  ParameterHandler parameterHandler) throws SQLException {
    if (null == params) {
      LogUtil.sql().debug("parameter is null");
      return;
    }
    LogUtil.sql().debug("parameter count: {}", params.size());
    for (int index = 0; index < params.size(); index++) {
      Object parameter = params.get(index);

      // 优先使用参数中的parameterHandler
      boolean result = false;
      if (null != parameterHandler) {
        result = parameterHandler.handle(conn, statement, index + 1, parameter);
      }

      if (result) {
        LogUtil.sql().trace("fill parameter: [{}] - [{}], use method parameter handler", index + 1, parameter);
      } else {
        if (null != parameter) {
          TypeSetHandler<?> handler = context.getSetHandlers().get(parameter.getClass());
          if (null != handler) {
            ((TypeSetHandler<Object>) handler).doSet(conn, statement, index + 1, parameter);
            LogUtil.sql().trace("fill parameter: [{}] - [{}], use global parameter handler", index + 1, parameter);
          } else {
            LogUtil.sql().trace("fill parameter: [{}] - [{}], use setObject", index + 1, parameter.getClass());
            statement.setObject(index + 1, parameter);
          }
        } else {
          LogUtil.sql().trace("fill parameter: [{}] - [null], use setNull", index + 1);
          statement.setNull(index + 1, Types.NULL);
        }
      }
    }
  }

  static PreparedStatement prepareStatement(Connection conn,
                                            String sql,
                                            int autoGeneratedKeys,
                                            StatementConfiguration configuration) throws SQLException {
    PreparedStatement ps = conn.prepareStatement(sql, autoGeneratedKeys);
    if (null != configuration) {
      if (null != configuration.getQueryTimeout()) {
        ps.setQueryTimeout(configuration.getQueryTimeout());
        LogUtil.common().debug("setQueryTimeout: {}", configuration.getQueryTimeout());
      }
      if (null != configuration.getFetchSize()) {
        ps.setFetchSize(configuration.getFetchSize());
        LogUtil.common().debug("setFetchSize: {}", configuration.getFetchSize());
      }
      if (null != configuration.getMaxRows()) {
        ps.setMaxRows(configuration.getMaxRows());
        LogUtil.common().debug("setMaxRows: {}", configuration.getMaxRows());
      }
    }
    return ps;
  }

  static PreparedStatement prepareStatement(Connection conn,
                                                    String sql,
                                                    StatementConfiguration configuration) throws SQLException {
    return prepareStatement(conn, sql, Statement.NO_GENERATED_KEYS, configuration);
  }

}

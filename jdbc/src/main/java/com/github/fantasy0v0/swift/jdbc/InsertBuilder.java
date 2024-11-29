package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.connection.ConnectionReference;
import com.github.fantasy0v0.swift.jdbc.exception.SwiftSQLException;
import com.github.fantasy0v0.swift.jdbc.util.LogUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static com.github.fantasy0v0.swift.jdbc.Utils.*;

public class InsertBuilder extends UpdateBuilder {

  InsertBuilder(DataSource dataSource, StatementConfiguration statementConfiguration, String sql) {
    super(dataSource, statementConfiguration, sql);
  }

  /**
   * 执行executeBatch, 并返回生成的主键
   * @param batchParams 参数
   * @param keyMapper 主键映射类
   * @param <T> 主键类型
   * @return 返回生成的主键
   */
  public <T> List<T> batch(List<List<Object>> batchParams, FetchMapper<T> keyMapper) {
    try (ConnectionReference ref = ConnectionPoolUtil.getReference(dataSource)) {
      return Utils.executeBatch(ref.unwrap(), statementConfiguration, sql, batchParams, parameterHandler, keyMapper);
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  private <T> T _fetchKey(FetchMapper<T> mapper,
                          List<Object> params) {
    try (ConnectionReference ref = ConnectionPoolUtil.getReference(dataSource)) {
      Connection conn = ref.unwrap();
      StopWatch stopWatch = new StopWatch();
      LogUtil.performance().info("fetchKey begin");
      String callerInfo = printCallerInfo();
      LogUtil.sql().debug("fetchKey: [{}], caller: {}", sql, callerInfo);
      try (PreparedStatement statement = prepareStatement(conn, sql, PreparedStatement.RETURN_GENERATED_KEYS, statementConfiguration)) {
        fillStatementParams(conn, statement, params, parameterHandler);
        int updated = statement.executeUpdate();
        LogUtil.sql().debug("executeUpdate: {}", updated);
        List<T> list = fetchByResultSet(statement.getGeneratedKeys(), mapper, true);
        return list.isEmpty() ? null : list.getFirst();
      } finally {
        LogUtil.performance().info("fetchKey end, cost: {}", stopWatch);
      }
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  public <T> T fetchKey(FetchMapper<T> mapper, List<Object> params) {
    return _fetchKey(mapper, params);
  }

  public <T> T fetchKey(FetchMapper<T> mapper, Object... params) {
    return fetchKey(mapper, Arrays.stream(params).toList());
  }

  public <T> T fetchKey(FetchMapper<T> mapper) {
    return fetchKey(mapper, (List<Object>) null);
  }

  public Object[] fetchKey(List<Object> params) {
    return fetchKey(Utils::fetchByRow, params);
  }

  public Object[] fetchKey(Object... params) {
    return fetchKey(Arrays.stream(params).toList());
  }

  public Object[] fetchKey() {
    return fetchKey(Utils::fetchByRow, (List<Object>) null);
  }
}

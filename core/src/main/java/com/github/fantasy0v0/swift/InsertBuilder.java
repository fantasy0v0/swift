package com.github.fantasy0v0.swift;

import com.github.fantasy0v0.swift.connection.ConnectionReference;
import com.github.fantasy0v0.swift.exception.SwiftSQLException;
import com.github.fantasy0v0.swift.util.LogUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static com.github.fantasy0v0.swift.Utils.*;

public class InsertBuilder extends UpdateBuilder {

  InsertBuilder(Context context, String sql) {
    super(context, sql);
  }

  /**
   * 执行executeBatch, 并返回生成的主键
   * @param batchParams 参数
   * @param keyMapper 主键映射类
   * @param <T> 主键类型
   * @return 返回生成的主键
   */
  public <T> List<T> batch(List<List<Object>> batchParams, RowMapper<T> keyMapper) {
    try (ConnectionReference ref = ConnectionPoolUtil.getReference(context)) {
      return Utils.executeBatch(
        context, ref.unwrap(), statementConfiguration, sql, batchParams,
        keyMapper
      );
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  private <T> T _fetchKey(RowMapper<T> mapper,
                          List<Object> params) {
    try (ConnectionReference ref = ConnectionPoolUtil.getReference(context)) {
      Connection conn = ref.unwrap();
      StopWatch stopWatch = new StopWatch();
      LogUtil.performance().trace("fetchKey begin");
      String callerInfo = printCallerInfo();
      LogUtil.sql().debug("fetchKey: [{}], caller: {}", sql, callerInfo);
      try (PreparedStatement statement = prepareStatement(conn, sql, PreparedStatement.RETURN_GENERATED_KEYS, statementConfiguration)) {
        fillStatementParams(context, conn, statement, params);
        int updated = statement.executeUpdate();
        LogUtil.sql().debug("executeUpdate: {}", updated);
        List<T> list = fetchByResultSet(
          statement.getGeneratedKeys(), context.getGetterMap(), mapper, true
        );
        return list.isEmpty() ? null : list.getFirst();
      } finally {
        LogUtil.performance().debug("fetchKey cost: {}", stopWatch);
      }
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

  public <T> T fetchKey(RowMapper<T> mapper, List<Object> params) {
    return _fetchKey(mapper, params);
  }

  public <T> T fetchKey(RowMapper<T> mapper, Object... params) {
    return fetchKey(mapper, Arrays.stream(params).toList());
  }

  public <T> T fetchKey(RowMapper<T> mapper) {
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

package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.dialect.SQLDialect;
import com.github.fantasy0v0.swift.jdbc.exception.SwiftException;
import com.github.fantasy0v0.swift.jdbc.exception.SwiftSQLException;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class PaginateBuilder {

  private final Context context;

  private final StatementConfiguration statementConfiguration;

  private final String sql;

  private final List<Object> params;

  /**
   * 页码从0开始
   */
  private final long pageNumber;

  private final long pageSize;

  private String countSql;

  private List<Object> countParams;

  PaginateBuilder(Context context, StatementConfiguration statementConfiguration,
                  String sql, List<Object> params, long number, long size) {
    this.context = context;
    this.statementConfiguration = statementConfiguration;
    this.sql = sql;
    this.params = params;
    this.pageNumber = number;
    this.pageSize = size;
  }

  public PaginateBuilder count(String countSql, Object...countParams) {
    this.countSql = countSql;
    this.countParams = Arrays.stream(countParams).toList();
    return this;
  }

  private Long getTotal() throws SQLException {
    String _countSql;
    List<Object> _countParams;
    if (null == countSql || countSql.isBlank()) {
      SQLDialect dialect = context.getSQLDialect();
      Query query = dialect.count(sql, params);
      _countSql = query.sql();
      _countParams = query.params();
    } else {
      _countSql = countSql;
      _countParams = countParams;
    }
    Long count = Utils.fetchOne(
      context, statementConfiguration,
      _countSql, _countParams,
      row -> row.getLong(1)
    );
    if (null == count) {
      throw new SwiftException("没有获取到总记录数, 请检查sql语句是否正确");
    }
    return count;
  }

  private <T> List<T> getData(FetchMapper<T> mapper) throws SQLException {
    SQLDialect dialect = context.getSQLDialect();
    Query query = dialect.paging(sql, params, pageNumber, pageSize);
    return Utils.fetch(
      context, statementConfiguration, query.sql(), query.params(), mapper
    );
  }

  public <T> PageData<T> fetch(FetchMapper<T> mapper) {
    try {
      long total = getTotal();
      long totalPages = 0;
      if (this.pageSize > 0) {
        totalPages = (total - 1) / this.pageSize + 1;
      }
      List<T> data = getData(mapper);
      return new PageData<>(total, totalPages, data);
    } catch (SQLException e) {
      throw new SwiftSQLException(e);
    }
  }

}

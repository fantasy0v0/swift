package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.exception.SwiftJdbcException;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PagingBuilder {

  private final DataSource dataSource;

  private final String sql;

  private final List<Object> params;

  private final long pageNumber;

  private final long pageSize;

  private String countSql;

  private List<Object> countParams;

  PagingBuilder(DataSource dataSource,
                String sql, List<Object> params,
                long number, long size) {
    this.dataSource = dataSource;
    this.sql = sql;
    this.params = params;
    this.pageNumber = number;
    this.pageSize = size;
  }

  public PagingBuilder count(String countSql, Object...countParams) {
    this.countSql = countSql;
    this.countParams = List.of(countParams);
    return this;
  }

  private Long getTotal() throws SQLException {
    String _countSql;
    List<Object> _countParams;
    if (null == countSql || countSql.isBlank()) {
      // TODO 后期根据方言生成SQL, 目前先适配H2和PostgreSQL
      _countSql = """
        select count(1) from (%s)""".formatted(sql);
      _countParams = params;
    } else {
      _countSql = countSql;
      _countParams = countParams;
    }
    Long count = Utils.fetchOne(dataSource, _countSql, _countParams,
      row -> row.getLong(1), null);
    if (null == count) {
      throw new SwiftJdbcException("没有获取到总记录数, 请检查sql语句是否正确");
    }
    return count;
  }

  private <T> List<T> getData(FetchMapper<T> mapper, ParameterProcess parameterProcess) throws SQLException {
    // TODO 后期根据方言生成SQL, 目前先适配H2和PostgreSQL
    List<Object> _params = new ArrayList<>();
    if (null != params && !params.isEmpty()) {
      _params.addAll(params);
    }
    long offset = this.pageNumber * this.pageSize;
    String _sql = """
      select * from (%s) offset ? row fetch first ? row only""".formatted(sql);
    _params.add(offset);
    _params.add(this.pageSize);
    return Utils.fetch(dataSource, _sql, _params, mapper, parameterProcess);
  }

  public <T> PagingData<T> fetch(FetchMapper<T> mapper, ParameterProcess parameterProcess) {
    try {
      long total = getTotal();
      long totalPages = 0;
      if (this.pageSize > 0) {
        totalPages = (total - 1) / this.pageSize + 1;
      }
      List<T> data = getData(mapper, parameterProcess);
      return new PagingData<>(total, totalPages, data);
    } catch (SQLException e) {
      throw new SwiftJdbcException(e);
    }
  }

  public <T> PagingData<T> fetch(FetchMapper<T> mapper) {
    return fetch(mapper, null);
  }

}

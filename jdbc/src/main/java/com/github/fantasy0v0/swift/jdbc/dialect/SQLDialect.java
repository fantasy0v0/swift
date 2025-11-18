package com.github.fantasy0v0.swift.jdbc.dialect;

import com.github.fantasy0v0.swift.jdbc.Query;

import javax.sql.DataSource;
import java.util.List;

public interface SQLDialect {

  void install(DataSource dataSource);

  Query count(String sql, List<Object> params);

  /**
   * @param sql        要分页的sql
   * @param params     参数
   * @param pageNumber 页码, 从0开始
   * @param pageSize   每页大小
   * @return Query
   */
  Query paging(String sql, List<Object> params, long pageNumber, long pageSize);

}

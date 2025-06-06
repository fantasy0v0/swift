package com.github.fantasy0v0.swift.jdbc.dialect;

import com.github.fantasy0v0.swift.jdbc.Query;
import com.github.fantasy0v0.swift.jdbc.predicate.Predicate;

import javax.sql.DataSource;
import java.util.List;

public interface SQLDialect {

  void install(DataSource dataSource);

  Query count(String sql, List<Object> params);

  Query paging(String sql, List<Object> params, long pageNumber, long pageSize);

}

package com.github.fantasy0v0.swift.jdbc.dialect;

import com.github.fantasy0v0.swift.jdbc.Query;

import java.util.ArrayList;
import java.util.List;

public class ANSI implements SQLDialect {

  public static final ANSI Instance = new ANSI();

  @Override
  public Query count(String sql, List<Object> params) {
    List<Object> countParams = new ArrayList<>();
    if (null != params && !params.isEmpty()) {
      countParams.addAll(params);
    }
    return new Query(
      "select count(1) from (%s)".formatted(sql),
      countParams
    );
  }

  @Override
  public Query paging(String sql, List<Object> params, long pageNumber, long pageSize) {
    List<Object> pagingParams = new ArrayList<>();
    if (null != params && !params.isEmpty()) {
      pagingParams.addAll(params);
    }
    long offset = pageNumber * pageSize;
    pagingParams.add(offset);
    pagingParams.add(pageSize);
    return new Query(
      "select * from (%s) offset ? row fetch first ? row only".formatted(sql),
      pagingParams
    );
  }
}

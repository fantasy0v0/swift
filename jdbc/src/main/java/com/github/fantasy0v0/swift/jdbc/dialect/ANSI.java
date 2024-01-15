package com.github.fantasy0v0.swift.jdbc.dialect;

import com.github.fantasy0v0.swift.jdbc.Query;
import com.github.fantasy0v0.swift.jdbc.predicate.Predicate;

import java.util.ArrayList;
import java.util.List;

public class ANSI implements SQLDialect {

  public static final ANSI Instance = new ANSI();

  @Override
  public Predicate equal(String expression, Object... params) {
    return null;
  }

  @Override
  public Predicate notEqual(String expression, Object... params) {
    return null;
  }

  @Override
  public Predicate lt(String expression, Object... params) {
    return null;
  }

  @Override
  public Predicate le(String expression, Object... params) {
    return null;
  }

  @Override
  public Predicate gt(String expression, Object... params) {
    return null;
  }

  @Override
  public Predicate ge(String expression, Object... params) {
    return null;
  }

  @Override
  public Predicate like(String expression, Object... params) {
    return null;
  }

  @Override
  public Predicate notLike(String expression, Object... params) {
    return null;
  }

  @Override
  public Predicate isNull(String expression, Object... params) {
    return null;
  }

  @Override
  public Predicate isNotNull(String expression, Object... params) {
    return null;
  }

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

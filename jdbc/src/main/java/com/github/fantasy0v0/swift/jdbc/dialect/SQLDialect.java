package com.github.fantasy0v0.swift.jdbc.dialect;

import com.github.fantasy0v0.swift.jdbc.Query;
import com.github.fantasy0v0.swift.jdbc.predicate.Predicate;

import java.util.List;

public interface SQLDialect {

  Predicate equal(String expression, Object... params);

  Predicate notEqual(String expression, Object... params);

  /**
   * less-than
   */
  Predicate lt(String expression, Object... params);

  /**
   * less-than-or-equal
   */
  Predicate le(String expression, Object... params);

  /**
   * greater-than
   */
  Predicate gt(String expression, Object... params);

  /**
   * greater-than-or-equal
   */
  Predicate ge(String expression, Object... params);

  Predicate like(String expression, Object... params);

  Predicate notLike(String expression, Object... params);

  Predicate isNull(String expression, Object... params);

  Predicate isNotNull(String expression, Object... params);

  Query count(String sql, List<Object> params);

  Query paging(String sql, List<Object> params, long pageNumber, long pageSize);

}

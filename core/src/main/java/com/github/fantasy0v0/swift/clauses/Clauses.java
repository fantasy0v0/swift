package com.github.fantasy0v0.swift.clauses;

import com.github.fantasy0v0.swift.predicate.ConjunctionPredicate;
import com.github.fantasy0v0.swift.predicate.Predicate;

public final class Clauses {

  public static String where(String sql, Predicate predicate) {
    String _sql;
    if (!(predicate instanceof ConjunctionPredicate)) {
      _sql = predicate.toSQL();
    } else {
      _sql = "";
    }
    if (!_sql.isBlank()) {
      if (sql.contains("where")) {
        sql += " and " + _sql;
      } else {
        sql += " where " + _sql;
      }
    }
    return sql;
  }

}

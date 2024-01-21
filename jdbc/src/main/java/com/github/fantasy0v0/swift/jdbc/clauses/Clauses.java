package com.github.fantasy0v0.swift.jdbc.clauses;

import com.github.fantasy0v0.swift.jdbc.predicate.ConjunctionPredicate;
import com.github.fantasy0v0.swift.jdbc.predicate.Predicate;
import org.intellij.lang.annotations.Language;

public final class Clauses {

  public static String where(@Language("SQL") String sql, Predicate predicate) {
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

package com.github.fantasy0v0.swift.jdbc.clauses;

import com.github.fantasy0v0.swift.jdbc.predicate.Predicate;

public final class Clauses {

  public static String where(Predicate predicate) {
    String sql = predicate.toSQL();
    if (!sql.isBlank()) {
      return "where " + sql;
    }
    return "";
  }

}

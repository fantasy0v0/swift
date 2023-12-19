package com.github.fantasy0v0.swift.jdbc.predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class AndPredicate implements Predicate {

  private final Predicate[] predicates;

  private String sql;

  private List<Object> parameters;

  AndPredicate(Predicate[] predicates) {
    this.predicates = predicates;
  }

  @Override
  public String toSQL() {
    if (null != sql) {
      return sql;
    }
    StringBuilder buff = new StringBuilder();
    for (Predicate predicate : predicates) {
      if (predicate instanceof ConjunctionPredicate && predicates.length > 1) {
        continue;
      }
      if (!buff.isEmpty()) {
        buff.append(" and ");
      }
      buff.append(predicate.toSQL());
    }
    sql = buff.toString();
    return sql;
  }

  @Override
  public List<Object> getParameters() {
    if (null != parameters) {
      return parameters;
    }
    parameters = Arrays.stream(predicates)
      .flatMap(p -> p.getParameters().stream()).toList();
    return parameters;
  }
}

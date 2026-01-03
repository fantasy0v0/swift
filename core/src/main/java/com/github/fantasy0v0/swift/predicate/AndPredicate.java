package com.github.fantasy0v0.swift.predicate;

import com.github.fantasy0v0.swift.util.LogUtil;

import java.util.Arrays;
import java.util.List;

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
        LogUtil.common().debug("conjunction ignore.");
        continue;
      }

      if (!buff.isEmpty()) {
        if (predicate instanceof OrPredicate or && 0 == or.size()) {
          continue;
        } else {
          buff.append(" and ");
        }
      }

      if (predicate instanceof OrPredicate or && or.size() > 1) {
        buff.append("(").append(predicate.toSQL()).append(")");
      } else {
        buff.append(predicate.toSQL());
      }
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

  public long size() {
    return predicates.length;
  }

}

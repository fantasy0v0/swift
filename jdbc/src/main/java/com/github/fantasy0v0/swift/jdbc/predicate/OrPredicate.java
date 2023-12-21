package com.github.fantasy0v0.swift.jdbc.predicate;

import com.github.fantasy0v0.swift.jdbc.util.LogUtil;

import java.util.Arrays;
import java.util.List;

public class OrPredicate implements Predicate {

  private final Predicate[] predicates;

  private String sql;

  private List<Object> parameters;

  OrPredicate(Predicate[] predicates) {
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
        buff.append(" or ");
      }

      if (predicate instanceof AndPredicate) {
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
}

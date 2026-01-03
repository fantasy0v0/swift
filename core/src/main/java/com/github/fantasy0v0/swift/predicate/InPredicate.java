package com.github.fantasy0v0.swift.predicate;

import com.github.fantasy0v0.swift.exception.SwiftException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InPredicate implements Predicate {

  private final boolean not;

  private final String expression;

  private final List<Object> parameters;

  private String sql;

  InPredicate(boolean not, String expression, List<Object> parameters) {
    this.not = not;
    this.expression = expression;
    if (parameters.isEmpty()) {
      throw new SwiftException("parameters为空");
    } else {
      this.parameters = parameters;
    }
  }

  @Override
  public String toSQL() {
    if (null != sql) {
      return sql;
    }
    StringBuilder buff = new StringBuilder();
    buff.append(expression);
    buff.append(" ");
    if (not) {
      buff.append("not in");
    } else {
      buff.append("in");
    }
    buff.append("(");
    buff.append(
      IntStream.range(0, parameters.size())
        .mapToObj(i -> "?")
        .collect(Collectors.joining(","))
    );
    buff.append(")");
    sql = buff.toString();
    return sql;
  }

  @Override
  public List<Object> getParameters() {
    return parameters;
  }

}

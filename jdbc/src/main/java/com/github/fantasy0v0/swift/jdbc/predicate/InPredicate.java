package com.github.fantasy0v0.swift.jdbc.predicate;

import java.util.List;

public class InPredicate implements Predicate {

  private final String expression;

  private final List<Object> parameters;

  InPredicate(String expression, List<Object> parameters) {
    this.expression = expression;
    this.parameters = parameters;
  }

  @Override
  public String toSQL() {
    return null;
  }

  @Override
  public List<Object> getParameters() {
    return parameters;
  }

  /**
   * @param expression 表达式
   * @return 是否是in 或 not in表达式
   */
  static boolean isInExpression(String expression) {
    return false;
  }

}

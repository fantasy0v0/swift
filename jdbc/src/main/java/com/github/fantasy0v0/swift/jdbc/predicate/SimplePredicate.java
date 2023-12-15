package com.github.fantasy0v0.swift.jdbc.predicate;

class SimplePredicate implements Predicate {

  private final String expression;

  private final Object[] parameters;

  SimplePredicate(String expression, Object[] parameters) {
    this.expression = expression;
    this.parameters = parameters;
  }

  @Override
  public String toSQL() {
    return expression;
  }

  @Override
  public Object[] getParameters() {
    return parameters;
  }
}

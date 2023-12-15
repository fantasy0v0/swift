package com.github.fantasy0v0.swift.jdbc.predicate;

class DisjunctionPredicate implements Predicate {

  @Override
  public String toSQL() {
    return "1 != 1";
  }

  @Override
  public Object[] getParameters() {
    return new Object[0];
  }
}

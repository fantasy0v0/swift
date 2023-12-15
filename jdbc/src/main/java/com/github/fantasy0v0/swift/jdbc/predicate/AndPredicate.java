package com.github.fantasy0v0.swift.jdbc.predicate;

class AndPredicate implements Predicate {

  private final Predicate[] predicates;

  AndPredicate(Predicate[] predicates) {
    this.predicates = predicates;
  }

  @Override
  public String toSQL() {
    return null;
  }

  @Override
  public Object[] getParameters() {
    return new Object[0];
  }
}

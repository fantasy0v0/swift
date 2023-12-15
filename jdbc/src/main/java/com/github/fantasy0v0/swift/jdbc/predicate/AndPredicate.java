package com.github.fantasy0v0.swift.jdbc.predicate;

import java.util.ArrayList;
import java.util.List;

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
  public List<Object> getParameters() {
    return new ArrayList<>();
  }
}

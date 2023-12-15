package com.github.fantasy0v0.swift.jdbc.predicate;

import java.util.ArrayList;
import java.util.List;

class DisjunctionPredicate implements Predicate {

  @Override
  public String toSQL() {
    return "1 != 1";
  }

  @Override
  public List<Object> getParameters() {
    return new ArrayList<>();
  }
}

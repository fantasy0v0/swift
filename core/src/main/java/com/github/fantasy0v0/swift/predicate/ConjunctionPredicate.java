package com.github.fantasy0v0.swift.predicate;

import java.util.Collections;
import java.util.List;

public class ConjunctionPredicate implements Predicate {

  @Override
  public String toSQL() {
    return "1 = 1";
  }

  @Override
  public List<Object> getParameters() {
    return Collections.emptyList();
  }
}

package com.github.fantasy0v0.swift.jdbc.predicate;

public final class Predicates {

  public static Predicate conjunction() {
    return new ConjunctionPredicate();
  }

  public static Predicate disjunction() {
    return new DisjunctionPredicate();
  }

  public static Predicate exp(String expression, Object...parameters) {
    return new SimplePredicate(expression, parameters);
  }

  public static Predicate and(Predicate... predicates) {
    return new AndPredicate(predicates);
  }

  public static Predicate or(Predicate... predicates) {
    return null;
  }

  public static Predicate not(Predicate predicate) {
    return null;
  }

}

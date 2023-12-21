package com.github.fantasy0v0.swift.jdbc.predicate;

import java.util.List;

public final class Predicates {

  public static Predicate conjunction() {
    return new ConjunctionPredicate();
  }

  @Deprecated
  public static Predicate disjunction() {
    return new DisjunctionPredicate();
  }

  public static Predicate exp(String expression, Object... parameters) {
    return new ExpressionPredicate(expression, parameters);
  }

  public static Predicate and(Predicate... predicates) {
    return new AndPredicate(predicates);
  }

  public static Predicate or(Predicate... predicates) {
    return new OrPredicate(predicates);
  }

  /*public static Predicate not(Predicate predicate) {
    return null;
  }*/

  public static Predicate in(String expression, Object... parameters) {
    return new InPredicate(false, expression, List.of(parameters));
  }

  public static Predicate notIn(String expression, Object... parameters) {
    return new InPredicate(true, expression, List.of(parameters));
  }

}

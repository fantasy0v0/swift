package com.github.fantasy0v0.swift.jdbc.predicate;

public final class Predicates {

  public static Predicate conjunction() {
    return new ConjunctionPredicate();
  }

  public static Predicate disjunction() {
    return new DisjunctionPredicate();
  }

  public static Predicate exp(String expression, Object...parameters) {
    // TODO 需要考虑?与参数是否匹配
    // TODO 需要考虑如果存在in关键字, 需要对表达式进行修改
    return new ExpressionPredicate(expression, parameters);
  }

  public static Predicate and(Predicate... predicates) {
    return new AndPredicate(predicates);
  }

  public static Predicate or(Predicate... predicates) {
    return null;
  }

  /*public static Predicate not(Predicate predicate) {
    return null;
  }*/

}

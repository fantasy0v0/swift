package com.github.fantasy0v0.swift.jdbc.predicate;

public class AndPredicate implements Predicate {

  private final Predicate[] predicates;

  AndPredicate(Predicate[] predicates) {
    this.predicates = predicates;
  }


}

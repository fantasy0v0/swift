package com.github.fantasy0v0.swift.predicate;

import java.util.List;

public interface Predicate {

  String toSQL();

  List<Object> getParameters();

}

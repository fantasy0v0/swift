package com.github.fantasy0v0.swift.jdbc;

public interface StatementConfigurator<T> {

  T setQueryTimeout(Integer queryTimeout);

  T setMaxFieldSize(Integer maxFieldSize);

  T setMaxRows(Integer maxRows);

}

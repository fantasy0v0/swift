package com.github.fantasy0v0.swift;

public interface StatementConfigurator<T> {

  T setQueryTimeout(Integer queryTimeout);

  T setFetchSize(Integer fetchSize);

  T setMaxRows(Integer maxRows);

}

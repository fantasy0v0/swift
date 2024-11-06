package com.github.fantasy0v0.swift.jdbc;

public interface StatementConfigurator {

  void setQueryTimeout(Integer queryTimeout);

  void setMaxFieldSize(Integer maxFieldSize);

  void setMaxRows(Integer maxRows);

}

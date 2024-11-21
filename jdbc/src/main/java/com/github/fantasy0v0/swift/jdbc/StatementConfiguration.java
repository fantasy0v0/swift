package com.github.fantasy0v0.swift.jdbc;

public class StatementConfiguration {

  private Integer queryTimeout;
  private Integer maxFieldSize;
  private Integer maxRows;

  public StatementConfiguration(Integer queryTimeout,
                                Integer maxFieldSize,
                                Integer maxRows) {
    this.queryTimeout = queryTimeout;
    this.maxFieldSize = maxFieldSize;
    this.maxRows = maxRows;
  }

  public StatementConfiguration() {

  }

  public Integer getQueryTimeout() {
    return queryTimeout;
  }

  public void setQueryTimeout(Integer queryTimeout) {
    this.queryTimeout = queryTimeout;
  }

  public Integer getMaxFieldSize() {
    return maxFieldSize;
  }

  public void setMaxFieldSize(Integer maxFieldSize) {
    this.maxFieldSize = maxFieldSize;
  }

  public Integer getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(Integer maxRows) {
    this.maxRows = maxRows;
  }
}

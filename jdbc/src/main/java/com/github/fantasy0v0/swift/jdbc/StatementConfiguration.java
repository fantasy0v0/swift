package com.github.fantasy0v0.swift.jdbc;

public class StatementConfiguration {

  private Integer queryTimeout;
  private Integer fetchSize;
  private Integer maxRows;

  public StatementConfiguration(Integer queryTimeout,
                                Integer fetchSize,
                                Integer maxRows) {
    this.queryTimeout = queryTimeout;
    this.fetchSize = fetchSize;
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

  public Integer getFetchSize() {
    return fetchSize;
  }

  public void setFetchSize(Integer fetchSize) {
    this.fetchSize = fetchSize;
  }

  public Integer getMaxRows() {
    return maxRows;
  }

  public void setMaxRows(Integer maxRows) {
    this.maxRows = maxRows;
  }
}

package com.github.fantasy0v0.swift.jdbc;

public record StatementConfiguration(Integer queryTimeout,
                                     Integer maxFieldSize,
                                     Integer maxRows) {

  public static class Builder {
    private Integer queryTimeout;
    private Integer maxFieldSize;
    private Integer maxRows;

    public Builder() {
    }

    public Builder queryTimeout(Integer queryTimeout) {
      this.queryTimeout = queryTimeout;
      return this;
    }

    public Builder maxFieldSize(Integer maxFieldSize) {
      this.maxFieldSize = maxFieldSize;
      return this;
    }

    public Builder maxRows(Integer maxRows) {
      this.maxRows = maxRows;
      return this;
    }

    public StatementConfiguration build() {
      return new StatementConfiguration(queryTimeout, maxFieldSize, maxRows);
    }
  }

}

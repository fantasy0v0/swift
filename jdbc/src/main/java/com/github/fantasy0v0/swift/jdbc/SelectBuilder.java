package com.github.fantasy0v0.swift.jdbc;

import java.util.List;

public class SelectBuilder {

  private final String sql;

  private final Object[] params;

  SelectBuilder(String sql, Object[] params) {
    this.sql = sql;
    this.params = params;
  }

  public void paging() {

  }

  public <T> List<T> fetch(Object mapper) {
    return null;
  }

  public <T> T fetchOne() {
    return null;
  }

}

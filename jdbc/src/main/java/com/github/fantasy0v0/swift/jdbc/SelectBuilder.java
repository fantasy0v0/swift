package com.github.fantasy0v0.swift.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SelectBuilder {

  private final DataSource dataSource;

  private final String sql;

  private final Object[] params;

  SelectBuilder(DataSource dataSource, String sql, Object[] params) {
    this.dataSource = dataSource;
    this.sql = sql;
    this.params = params;
  }

  public void paging() {

  }

  private <T> List<T> _fetch(FetchMapper<T> mapper) throws SQLException {

    try (Connection conn = Utils.getConnection(dataSource)) {
      PreparedStatement statement = conn.prepareStatement(sql);
      if (0 == params.length) {

      } else {

      }
    }

    return null;

  }

  public <T> List<T> fetch(FetchMapper<T> mapper) {
    return null;
  }

  public <T> T fetchOne(FetchMapper<T> mapper) {
    return null;
  }

}

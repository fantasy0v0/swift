package com.github.fantasy0v0.swift.jdbc;

import javax.sql.DataSource;

public class UpdateBuilder extends InsertBuilder {

  UpdateBuilder(DataSource dataSource, String sql) {
    super(dataSource, sql);
  }

}

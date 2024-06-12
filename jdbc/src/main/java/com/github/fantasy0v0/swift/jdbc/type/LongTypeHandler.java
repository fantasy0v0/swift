package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.*;

public class LongTypeHandler implements TypeSetHandler<Long> {

  @Override
  public Class<Long> support() {
    return Long.class;
  }

  @Override
  public void doSet(Connection con, PreparedStatement ps, int index, Long parameter) throws SQLException {
    ps.setLong(index, parameter);
  }
}

package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.*;

public class IntegerTypeHandler implements TypeSetHandler<Integer> {

  @Override
  public Class<Integer> support() {
    return Integer.class;
  }

  @Override
  public void doSet(Connection con, PreparedStatement ps, int index, Integer parameter) throws SQLException {
    ps.setInt(index, parameter);
  }
}

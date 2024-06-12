package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.*;

public class StringTypeHandler implements TypeSetHandler<String> {

  @Override
  public Class<String> support() {
    return String.class;
  }

  @Override
  public void doSet(Connection con, PreparedStatement ps, int index, String parameter) throws SQLException {
    ps.setString(index, parameter);
  }
}

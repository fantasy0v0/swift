package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.*;

public class StringTypeHandler extends AbstractTypeHandler<String> {

  public StringTypeHandler() {
    super(String.class, Types.VARCHAR);
  }

  @Override
  protected String doGetInternal(ResultSet resultSet, int columnIndex) throws SQLException {
    return extract(resultSet, columnIndex, resultSet::getString);
  }

  @Override
  protected void doSetInternal(Connection con, PreparedStatement ps, int index, String parameter) throws SQLException {
    ps.setString(index, parameter);
  }
}

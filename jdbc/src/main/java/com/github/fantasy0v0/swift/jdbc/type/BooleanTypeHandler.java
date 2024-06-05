package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.*;

public class BooleanTypeHandler extends TypeHandler<Boolean> {

  public BooleanTypeHandler() {
    super(Types.BOOLEAN);
  }

  @Override
  protected Boolean doGet(ResultSet resultSet, int columnIndex) throws SQLException {
    return extract(resultSet, rs -> rs.getBoolean(columnIndex));
  }

  @Override
  protected void doSet(Connection con, PreparedStatement ps, int index, Boolean parameter) throws SQLException {
    ps.setBoolean(index, parameter);
  }
}

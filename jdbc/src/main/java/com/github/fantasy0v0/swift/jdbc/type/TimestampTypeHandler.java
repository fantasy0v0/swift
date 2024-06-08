package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.*;

public class TimestampTypeHandler extends AbstractTypeHandler<Timestamp> {

  public TimestampTypeHandler() {
    super(Timestamp.class, Types.TIMESTAMP);
  }

  @Override
  protected Timestamp doGetInternal(ResultSet resultSet, int columnIndex) throws SQLException {
    return extract(resultSet, columnIndex, resultSet::getTimestamp);
  }

  @Override
  protected void doSetInternal(Connection con, PreparedStatement ps, int index, Timestamp parameter) throws SQLException {
    ps.setTimestamp(index, parameter);
  }
}

package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.*;

public class TimestampTypeHandler implements TypeSetHandler<Timestamp> {

  @Override
  public Class<Timestamp> support() {
    return Timestamp.class;
  }

  @Override
  public void doSet(Connection con, PreparedStatement ps, int index, Timestamp parameter) throws SQLException {
    ps.setTimestamp(index, parameter);
  }
}

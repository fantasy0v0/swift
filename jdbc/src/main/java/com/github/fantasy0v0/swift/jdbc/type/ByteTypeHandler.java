package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.*;

public class ByteTypeHandler implements TypeSetHandler<Byte> {

  @Override
  public Class<Byte> support() {
    return Byte.class;
  }

  @Override
  public void doSet(Connection con, PreparedStatement ps, int index, Byte parameter) throws SQLException {
    ps.setByte(index, parameter);
  }
}

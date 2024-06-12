package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.*;

public class ByteArrayTypeHandler implements TypeSetHandler<byte[]> {

  @Override
  public Class<byte[]> support() {
    return byte[].class;
  }

  @Override
  public void doSet(Connection con, PreparedStatement ps, int index, byte[] parameter) throws SQLException {
    ps.setBytes(index, parameter);
  }
}

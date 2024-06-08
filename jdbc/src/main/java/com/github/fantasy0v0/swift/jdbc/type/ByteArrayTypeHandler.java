package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.*;

public class ByteArrayTypeHandler extends AbstractTypeHandler<byte[]> {

  public ByteArrayTypeHandler() {
    super (byte[].class, Types.VARBINARY);
  }

  @Override
  protected byte[] doGetInternal(ResultSet resultSet, int columnIndex) throws SQLException {
    return extract(resultSet, columnIndex, resultSet::getBytes);
  }

  @Override
  protected void doSetInternal(Connection con, PreparedStatement ps, int index, byte[] parameter) throws SQLException {
    ps.setBytes(index, parameter);
  }
}

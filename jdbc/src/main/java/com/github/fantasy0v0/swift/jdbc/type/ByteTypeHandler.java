package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.*;

public class ByteTypeHandler extends AbstractTypeHandler<Byte> {

  public ByteTypeHandler() {
    super(Byte.class, Types.TINYINT);
  }

  @Override
  protected Byte doGetInternal(ResultSet resultSet, int columnIndex) throws SQLException {
    return extract(resultSet, columnIndex, resultSet::getByte);
  }

  @Override
  protected void doSetInternal(Connection con, PreparedStatement ps, int index, Byte parameter) throws SQLException {
    ps.setByte(index, parameter);
  }
}

package com.github.fantasy0v0.swift.jdbc.typehandles;

import com.github.fantasy0v0.swift.jdbc.TypeHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ByteTypeHandler implements TypeHandler<Byte> {

  @Override
  public Class<Byte> supported() {
    return Byte.class;
  }

  @Override
  public boolean handle(Connection conn, PreparedStatement statement, int index, Byte parameter) throws SQLException {
    statement.setByte(index, parameter);
    return true;
  }
}

package com.github.fantasy0v0.swift.jdbc.typehandles;

import com.github.fantasy0v0.swift.jdbc.TypeHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ShortTypeHandler implements TypeHandler<Short> {

  @Override
  public Class<Short> supported() {
    return Short.class;
  }

  @Override
  public boolean handle(Connection conn, PreparedStatement statement, int index, Short parameter) throws SQLException {
    statement.setShort(index, parameter);
    return true;
  }
}

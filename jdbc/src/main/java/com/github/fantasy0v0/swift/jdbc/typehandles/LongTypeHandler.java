package com.github.fantasy0v0.swift.jdbc.typehandles;

import com.github.fantasy0v0.swift.jdbc.TypeHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LongTypeHandler implements TypeHandler<Long> {

  @Override
  public Class<Long> supported() {
    return Long.class;
  }

  @Override
  public boolean handle(Connection conn, PreparedStatement statement, int index, Long parameter) throws SQLException {
    statement.setLong(index, parameter);
    return true;
  }
}

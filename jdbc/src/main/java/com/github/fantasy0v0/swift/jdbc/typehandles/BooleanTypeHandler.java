package com.github.fantasy0v0.swift.jdbc.typehandles;

import com.github.fantasy0v0.swift.jdbc.TypeHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BooleanTypeHandler implements TypeHandler<Boolean> {

  @Override
  public Class<Boolean> supported() {
    return Boolean.class;
  }

  @Override
  public boolean handle(Connection conn, PreparedStatement statement, int index, Boolean parameter) throws SQLException {
    statement.setBoolean(index, parameter);
    return true;
  }
}

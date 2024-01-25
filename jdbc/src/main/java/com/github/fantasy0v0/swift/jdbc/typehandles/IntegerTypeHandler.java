package com.github.fantasy0v0.swift.jdbc.typehandles;

import com.github.fantasy0v0.swift.jdbc.TypeHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IntegerTypeHandler implements TypeHandler<Integer> {

  @Override
  public Class<Integer> supported() {
    return Integer.class;
  }

  @Override
  public boolean handle(Connection conn, PreparedStatement statement, int index, Integer parameter) throws SQLException {
    statement.setInt(index, parameter);
    return true;
  }
}

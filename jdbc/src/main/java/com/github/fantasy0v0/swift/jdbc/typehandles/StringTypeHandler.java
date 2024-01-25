package com.github.fantasy0v0.swift.jdbc.typehandles;

import com.github.fantasy0v0.swift.jdbc.TypeHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StringTypeHandler implements TypeHandler<String> {

  @Override
  public Class<String> supported() {
    return String.class;
  }

  @Override
  public boolean handle(Connection conn, PreparedStatement statement, int index, String parameter) throws SQLException {
    statement.setString(index, parameter);
    return true;
  }
}

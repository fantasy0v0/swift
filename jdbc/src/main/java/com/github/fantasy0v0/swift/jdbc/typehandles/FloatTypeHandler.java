package com.github.fantasy0v0.swift.jdbc.typehandles;

import com.github.fantasy0v0.swift.jdbc.TypeHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FloatTypeHandler implements TypeHandler<Float> {

  @Override
  public Class<Float> supported() {
    return Float.class;
  }

  @Override
  public boolean handle(Connection conn, PreparedStatement statement, int index, Float parameter) throws SQLException {
    statement.setFloat(index, parameter);
    return true;
  }
}

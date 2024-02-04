package com.github.fantasy0v0.swift.jdbc.typehandles;

import com.github.fantasy0v0.swift.jdbc.TypeHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class TimestampTypeHandler implements TypeHandler<Timestamp> {

  @Override
  public Class<Timestamp> supported() {
    return Timestamp.class;
  }

  @Override
  public boolean handle(Connection conn, PreparedStatement statement, int index, Timestamp parameter) throws SQLException {
    statement.setTimestamp(index, parameter);
    return true;
  }
}

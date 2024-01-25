package com.github.fantasy0v0.swift.jdbc.typehandles;

import com.github.fantasy0v0.swift.jdbc.TypeHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class LocalDateTimeTypeHandler implements TypeHandler<LocalDateTime> {

  @Override
  public Class<LocalDateTime> supported() {
    return LocalDateTime.class;
  }

  @Override
  public boolean handle(Connection conn, PreparedStatement statement, int index, LocalDateTime parameter) throws SQLException {
    statement.setTimestamp(index, Timestamp.valueOf(parameter));
    return true;
  }
}

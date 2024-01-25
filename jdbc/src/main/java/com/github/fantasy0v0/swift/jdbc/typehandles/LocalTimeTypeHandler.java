package com.github.fantasy0v0.swift.jdbc.typehandles;

import com.github.fantasy0v0.swift.jdbc.TypeHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;

public class LocalTimeTypeHandler implements TypeHandler<LocalTime> {

  @Override
  public Class<LocalTime> supported() {
    return LocalTime.class;
  }

  @Override
  public boolean handle(Connection conn, PreparedStatement statement, int index, LocalTime parameter) throws SQLException {
    statement.setTime(index, Time.valueOf(parameter));
    return true;
  }
}

package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;

public class LocalTimeTypeHandler implements TypeSetHandler<LocalTime> {

  @Override
  public Class<LocalTime> support() {
    return LocalTime.class;
  }

  @Override
  public void doSet(Connection conn, PreparedStatement statement, int index, LocalTime parameter) throws SQLException {
    statement.setTime(index, Time.valueOf(parameter));
  }
}

package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class LocalDateTimeTypeHandler implements TypeSetHandler<LocalDateTime> {

  @Override
  public Class<LocalDateTime> support() {
    return LocalDateTime.class;
  }

  @Override
  public void doSet(Connection con, PreparedStatement ps, int index, LocalDateTime parameter) throws SQLException {
    ps.setTimestamp(index, Timestamp.valueOf(parameter));
  }
}

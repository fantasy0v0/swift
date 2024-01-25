package com.github.fantasy0v0.swift.jdbc.typehandles;

import com.github.fantasy0v0.swift.jdbc.TypeHandler;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class LocalDateTypeHandler implements TypeHandler<LocalDate> {

  @Override
  public Class<LocalDate> supported() {
    return LocalDate.class;
  }

  @Override
  public boolean handle(Connection conn, PreparedStatement statement, int index, LocalDate parameter) throws SQLException {
    statement.setDate(index, Date.valueOf(parameter));
    return true;
  }
}

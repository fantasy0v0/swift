package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class LocalDateTypeHandler implements TypeSetHandler<LocalDate> {

  @Override
  public Class<LocalDate> support() {
    return LocalDate.class;
  }

  @Override
  public void doSet(Connection con, PreparedStatement ps, int index, LocalDate parameter) throws SQLException {
    ps.setDate(index, Date.valueOf(parameter));
  }
}

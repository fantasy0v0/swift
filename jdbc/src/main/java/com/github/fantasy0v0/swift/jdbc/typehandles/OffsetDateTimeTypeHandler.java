package com.github.fantasy0v0.swift.jdbc.typehandles;

import com.github.fantasy0v0.swift.jdbc.TypeHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class OffsetDateTimeTypeHandler implements TypeHandler<OffsetDateTime> {

  @Override
  public Class<OffsetDateTime> supported() {
    return OffsetDateTime.class;
  }

  @Override
  public boolean handle(Connection conn, PreparedStatement statement, int index, OffsetDateTime parameter) throws SQLException {
    LocalDateTime localDateTime = parameter.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    statement.setTimestamp(index, Timestamp.valueOf(localDateTime));
    return true;
  }
}

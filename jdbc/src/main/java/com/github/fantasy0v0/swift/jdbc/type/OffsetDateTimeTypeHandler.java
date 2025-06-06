package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class OffsetDateTimeTypeHandler extends AbstractTypeHandler<OffsetDateTime> {

  public OffsetDateTimeTypeHandler() {
    super(OffsetDateTime.class, Types.TIMESTAMP_WITH_TIMEZONE);
  }

  @Override
  protected OffsetDateTime doGetInternal(ResultSet resultSet, int columnIndex) throws SQLException {
    Timestamp value = extract(resultSet, columnIndex, resultSet::getTimestamp);
    if (null == value) {
      return null;
    }
    ZoneId systemZoneId = ZoneId.systemDefault();
    ZoneOffset systemZoneOffset = systemZoneId.getRules().getOffset(value.toInstant());
    return value.toInstant().atOffset(systemZoneOffset);
  }

  @Override
  protected void doSetInternal(Connection con, PreparedStatement ps, int index, OffsetDateTime parameter) throws SQLException {
    ps.setTimestamp(index, Timestamp.from(parameter.toInstant()));
  }
}

package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.*;

public class LongTypeHandler extends AbstractTypeHandler<Long> {

  public LongTypeHandler() {
    super(Long.class, Types.BIGINT);
  }

  @Override
  protected Long doGetInternal(ResultSet resultSet, int columnIndex) throws SQLException {
    return extract(resultSet, columnIndex, resultSet::getLong);
  }

  @Override
  protected void doSetInternal(Connection con, PreparedStatement ps, int index, Long parameter) throws SQLException {

  }
}

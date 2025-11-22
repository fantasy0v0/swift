package test.parameter.handler;

import com.github.fantasy0v0.swift.jdbc.parameter.AbstractParameterHandler;
import com.github.fantasy0v0.swift.jdbc.parameter.ParameterMetaData;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * 备用
 *
 * @author fan 2025/10/14
 */
public class OffsetDateTimeHandler extends AbstractParameterHandler<OffsetDateTime> {

  public OffsetDateTimeHandler() {
    super(OffsetDateTime.class, Types.TIMESTAMP_WITH_TIMEZONE);
  }

  @Override
  public OffsetDateTime get(ParameterMetaData metaData, Object parameter) throws SQLException {
    if (parameter instanceof Timestamp value) {
      ZoneId systemZoneId = ZoneId.systemDefault();
      ZoneOffset systemZoneOffset = systemZoneId.getRules().getOffset(value.toInstant());
      return value.toInstant().atOffset(systemZoneOffset);
    }
    throw new SQLException("Unsupported parameter type: " + parameter.getClass().getName());
  }

  @Override
  public void set(Connection connection, OffsetDateTime parameter,
                  PreparedStatement statement, int columnIndex) throws SQLException {
    statement.setTimestamp(columnIndex, Timestamp.from(parameter.toInstant()));
  }
}

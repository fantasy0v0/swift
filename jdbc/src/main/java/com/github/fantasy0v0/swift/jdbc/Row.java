package com.github.fantasy0v0.swift.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.*;

public class Row {

  private final ResultSet resultSet;

  Row(ResultSet resultSet) {
    this.resultSet = resultSet;
  }

  public int getColumnCount() throws SQLException {
    return resultSet.getMetaData().getColumnCount();
  }

  public String getColumnLabel(int column) throws SQLException {
    return resultSet.getMetaData().getColumnLabel(column);
  }

  public int getColumnType(int column) throws SQLException {
    return resultSet.getMetaData().getColumnType(column);
  }

  public int findColumnIndex(String expected) throws SQLException {
    if (null == expected || expected.isBlank()) {
      return -1;
    }
    for (int index = 1; index <= getColumnCount(); index++) {
      String actual = getColumnLabel(index);
      if (expected.equals(actual)) {
        return index;
      }
    }
    return -1;
  }

  private <T> T extract(FunctionWithException<T> function) throws SQLException {
    T value = function.apply(resultSet);
    return resultSet.wasNull() ? null : value;
  }

  public Array getArray(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getArray(columnIndex));
  }

  public Array getArray(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getArray(columnLabel));
  }

  public InputStream getAsciiStream(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getAsciiStream(columnIndex));
  }

  public InputStream getAsciiStream(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getAsciiStream(columnLabel));
  }

  public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getBigDecimal(columnIndex));
  }

  public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getBigDecimal(columnLabel));
  }

  public InputStream getBinaryStream(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getBinaryStream(columnIndex));
  }

  public InputStream getBinaryStream(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getBinaryStream(columnLabel));
  }

  public Blob getBlob(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getBlob(columnIndex));
  }

  public Blob getBlob(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getBlob(columnLabel));
  }

  public Boolean getBoolean(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getBoolean(columnIndex));
  }

  public Boolean getBoolean(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getBoolean(columnLabel));
  }

  public Byte getByte(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getByte(columnIndex));
  }

  public Byte getByte(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getByte(columnLabel));
  }

  public byte[] getBytes(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getBytes(columnIndex));
  }

  public byte[] getBytes(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getBytes(columnLabel));
  }

  public Reader getCharacterStream(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getCharacterStream(columnIndex));
  }

  public Reader getCharacterStream(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getCharacterStream(columnLabel));
  }

  public Clob getClob(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getClob(columnIndex));
  }

  public Clob getClob(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getClob(columnLabel));
  }

  public Date getDate(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getDate(columnIndex));
  }

  public Date getDate(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getDate(columnLabel));
  }

  public Double getDouble(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getDouble(columnIndex));
  }

  public Double getDouble(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getDouble(columnLabel));
  }

  public Float getFloat(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getFloat(columnIndex));
  }

  public Float getFloat(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getFloat(columnLabel));
  }

  public Integer getInt(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getInt(columnIndex));
  }

  public Integer getInt(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getInt(columnLabel));
  }

  public Long getLong(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getLong(columnIndex));
  }

  public Long getLong(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getLong(columnLabel));
  }

  public Reader getNCharacterStream(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getNCharacterStream(columnIndex));
  }

  public Reader getNCharacterStream(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getNCharacterStream(columnLabel));
  }

  public NClob getNClob(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getNClob(columnIndex));
  }

  public NClob getNClob(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getNClob(columnLabel));
  }

  public String getNString(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getNString(columnIndex));
  }

  public String getNString(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getNString(columnLabel));
  }

  public Object getObject(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getObject(columnIndex));
  }

  public Object getObject(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getObject(columnLabel));
  }

  public Ref getRef(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getRef(columnIndex));
  }

  public Ref getRef(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getRef(columnLabel));
  }

  public RowId getRowId(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getRowId(columnIndex));
  }

  public RowId getRowId(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getRowId(columnLabel));
  }

  public SQLXML getSQLXML(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getSQLXML(columnIndex));
  }

  public SQLXML getSQLXML(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getSQLXML(columnLabel));
  }

  public Short getShort(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getShort(columnIndex));
  }

  public Short getShort(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getShort(columnLabel));
  }

  public String getString(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getString(columnIndex));
  }

  public String getString(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getString(columnLabel));
  }

  public Time getTime(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getTime(columnIndex));
  }

  public Time getTime(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getTime(columnLabel));
  }

  public Timestamp getTimestamp(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getTimestamp(columnIndex));
  }

  public Timestamp getTimestamp(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getTimestamp(columnLabel));
  }

  public URL getURL(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getURL(columnIndex));
  }

  public URL getURL(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getURL(columnLabel));
  }

  public LocalTime getLocalTime(int columnIndex) throws SQLException {
    Time value = extract(resultSet -> resultSet.getTime(columnIndex));
    return null != value ? value.toLocalTime() : null;
  }

  public LocalTime getLocalTime(String columnLabel) throws SQLException {
    Time value = extract(resultSet -> resultSet.getTime(columnLabel));
    return null != value ? value.toLocalTime() : null;
  }

  public LocalDate getLocalDate(int columnIndex) throws SQLException {
    Date value = extract(resultSet -> resultSet.getDate(columnIndex));
    return null != value ? value.toLocalDate() : null;
  }

  public LocalDate getLocalDate(String columnLabel) throws SQLException {
    Date value = extract(resultSet -> resultSet.getDate(columnLabel));
    return null != value ? value.toLocalDate() : null;
  }

  public LocalDateTime getLocalDateTime(int columnIndex) throws SQLException {
    Timestamp value = extract(resultSet -> resultSet.getTimestamp(columnIndex));
    return null != value ? value.toLocalDateTime() : null;
  }

  public LocalDateTime getLocalDateTime(String columnLabel) throws SQLException {
    Timestamp value = extract(resultSet -> resultSet.getTimestamp(columnLabel));
    return null != value ? value.toLocalDateTime() : null;
  }

  public OffsetDateTime getOffsetDateTime(int columnIndex) throws SQLException {
    Timestamp value = extract(resultSet -> resultSet.getTimestamp(columnIndex));
    if (null != value) {
      return OffsetDateTime.ofInstant(value.toInstant(), ZoneOffset.UTC);
    }
    return null;
  }

  public OffsetDateTime getOffsetDateTime(String columnLabel) throws SQLException {
    Timestamp value = extract(resultSet -> resultSet.getTimestamp(columnLabel));
    if (null != value) {
      return OffsetDateTime.ofInstant(value.toInstant(), ZoneOffset.UTC);
    }
    return null;
  }

  public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
    return extract(resultSet -> resultSet.getObject(columnIndex, type));
  }

  public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
    return extract(resultSet -> resultSet.getObject(columnLabel, type));
  }
}

interface FunctionWithException<R> {

  R apply(ResultSet resultSet) throws SQLException;

}

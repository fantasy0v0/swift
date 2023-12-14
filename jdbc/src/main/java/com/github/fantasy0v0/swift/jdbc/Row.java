package com.github.fantasy0v0.swift.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.net.URL;
import java.sql.Timestamp;
import java.sql.SQLXML;
import java.io.Reader;
import java.sql.Array;
import java.sql.Date;
import java.math.BigDecimal;
import java.sql.RowId;
import java.sql.Ref;
import java.sql.NClob;
import java.sql.Blob;
import java.sql.Clob;
import java.io.InputStream;
import java.time.LocalTime;

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

  private <T> T coalesce(FunctionWithException<T> function) throws SQLException {
    T value = function.apply(resultSet);
    return resultSet.wasNull() ? null : value;
  }

  public Array getArray(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getArray(columnIndex));
  }

  public Array getArray(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getArray(columnLabel));
  }

  public InputStream getAsciiStream(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getAsciiStream(columnIndex));
  }

  public InputStream getAsciiStream(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getAsciiStream(columnLabel));
  }

  public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getBigDecimal(columnIndex));
  }

  public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getBigDecimal(columnLabel));
  }

  public InputStream getBinaryStream(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getBinaryStream(columnIndex));
  }

  public InputStream getBinaryStream(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getBinaryStream(columnLabel));
  }

  public Blob getBlob(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getBlob(columnIndex));
  }

  public Blob getBlob(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getBlob(columnLabel));
  }

  public Boolean getBoolean(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getBoolean(columnIndex));
  }

  public Boolean getBoolean(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getBoolean(columnLabel));
  }

  public Byte getByte(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getByte(columnIndex));
  }

  public Byte getByte(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getByte(columnLabel));
  }

  public byte[] getBytes(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getBytes(columnIndex));
  }

  public byte[] getBytes(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getBytes(columnLabel));
  }

  public Reader getCharacterStream(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getCharacterStream(columnIndex));
  }

  public Reader getCharacterStream(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getCharacterStream(columnLabel));
  }

  public Clob getClob(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getClob(columnIndex));
  }

  public Clob getClob(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getClob(columnLabel));
  }

  public Date getDate(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getDate(columnIndex));
  }

  public Date getDate(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getDate(columnLabel));
  }

  public Double getDouble(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getDouble(columnIndex));
  }

  public Double getDouble(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getDouble(columnLabel));
  }

  public Float getFloat(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getFloat(columnIndex));
  }

  public Float getFloat(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getFloat(columnLabel));
  }

  public Integer getInt(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getInt(columnIndex));
  }

  public Integer getInt(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getInt(columnLabel));
  }

  public Long getLong(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getLong(columnIndex));
  }

  public Long getLong(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getLong(columnLabel));
  }

  public Reader getNCharacterStream(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getNCharacterStream(columnIndex));
  }

  public Reader getNCharacterStream(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getNCharacterStream(columnLabel));
  }

  public NClob getNClob(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getNClob(columnIndex));
  }

  public NClob getNClob(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getNClob(columnLabel));
  }

  public String getNString(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getNString(columnIndex));
  }

  public String getNString(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getNString(columnLabel));
  }

  public Object getObject(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getObject(columnIndex));
  }

  public Object getObject(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getObject(columnLabel));
  }

  public Ref getRef(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getRef(columnIndex));
  }

  public Ref getRef(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getRef(columnLabel));
  }

  public RowId getRowId(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getRowId(columnIndex));
  }

  public RowId getRowId(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getRowId(columnLabel));
  }

  public SQLXML getSQLXML(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getSQLXML(columnIndex));
  }

  public SQLXML getSQLXML(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getSQLXML(columnLabel));
  }

  public Short getShort(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getShort(columnIndex));
  }

  public Short getShort(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getShort(columnLabel));
  }

  public String getString(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getString(columnIndex));
  }

  public String getString(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getString(columnLabel));
  }

  public Time getTime(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getTime(columnIndex));
  }

  public Time getTime(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getTime(columnLabel));
  }

  public Timestamp getTimestamp(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getTimestamp(columnIndex));
  }

  public Timestamp getTimestamp(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getTimestamp(columnLabel));
  }

  public URL getURL(int columnIndex) throws SQLException {
    return coalesce(resultSet -> resultSet.getURL(columnIndex));
  }

  public URL getURL(String columnLabel) throws SQLException {
    return coalesce(resultSet -> resultSet.getURL(columnLabel));
  }

  public LocalTime getLocalTime(int columnIndex) throws SQLException {
    Time value = coalesce(resultSet -> resultSet.getTime(columnIndex));
    return null != value ? value.toLocalTime() : null;
  }
}

interface FunctionWithException<R> {

  R apply(ResultSet resultSet) throws SQLException;

}

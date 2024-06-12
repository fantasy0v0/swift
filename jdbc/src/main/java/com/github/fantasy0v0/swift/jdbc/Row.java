package com.github.fantasy0v0.swift.jdbc;

import com.github.fantasy0v0.swift.jdbc.type.TypeGetHandler;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.util.Map;

public class Row {

  private final ResultSet resultSet;

  private final Map<Class<?>, TypeGetHandler<?>> handlerMap;

  Row(ResultSet resultSet, Map<Class<?>, TypeGetHandler<?>> handlerMap) {
    this.resultSet = resultSet;
    this.handlerMap = handlerMap;
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

  public int findColumn(String columnLabel) throws SQLException {
    return resultSet.findColumn(columnLabel);
  }

  private <T> T extract(FunctionWithException<T> function) throws SQLException {
    T value = function.apply(resultSet);
    return resultSet.wasNull() ? null : value;
  }

  private <P> P extract(ResultSet resultSet, int columnIndex, ResultSetExtractFunction<P> function) throws SQLException {
    P value = function.apply(columnIndex);
    return resultSet.wasNull() ? null : value;
  }

  @SuppressWarnings("unchecked")
  private <T> TypeGetHandler<T> getHandler(Class<T> type, TypeGetHandler<T> handler) {
    if (null != handler) {
      return handler;
    }
    if (null == handlerMap || handlerMap.isEmpty()) {
      return null;
    }
    return (TypeGetHandler<T>)handlerMap.get(type);
  }

  public Object getObject(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getObject(columnIndex));
  }

  public Object getObject(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getObject(columnLabel));
  }

  public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
    return extract(resultSet -> resultSet.getObject(columnIndex, type));
  }

  public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
    return extract(resultSet -> resultSet.getObject(columnLabel, type));
  }

  public <T> T getByFunction(FunctionWithException<T> function) throws SQLException {
    if (null == function) {
      return null;
    }
    return function.apply(resultSet);
  }

  public LocalTime getLocalTime(int columnIndex, TypeGetHandler<LocalTime> handler) throws SQLException {
    handler = getHandler(LocalTime.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    Time value = extract(resultSet -> resultSet.getTime(columnIndex));
    return null != value ? value.toLocalTime() : null;
  }

  public LocalTime getLocalTime(String columnLabel, TypeGetHandler<LocalTime> handler) throws SQLException {
    return getLocalTime(resultSet.findColumn(columnLabel), handler);
  }

  public LocalTime getLocalTime(int columnIndex) throws SQLException {
    return getLocalTime(columnIndex, null);
  }

  public LocalTime getLocalTime(String columnLabel) throws SQLException {
    return getLocalTime(resultSet.findColumn(columnLabel), null);
  }

  public LocalDate getLocalDate(int columnIndex, TypeGetHandler<LocalDate> handler) throws SQLException {
    handler = getHandler(LocalDate.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    Date value = extract(resultSet -> resultSet.getDate(columnIndex));
    return null != value ? value.toLocalDate() : null;
  }

  public LocalDate getLocalDate(String columnLabel, TypeGetHandler<LocalDate> handler) throws SQLException {
    return getLocalDate(resultSet.findColumn(columnLabel), handler);
  }

  public LocalDate getLocalDate(int columnIndex) throws SQLException {
    return getLocalDate(columnIndex, null);
  }

  public LocalDate getLocalDate(String columnLabel) throws SQLException {
    return getLocalDate(resultSet.findColumn(columnLabel), null);
  }

  public LocalDateTime getLocalDateTime(int columnIndex, TypeGetHandler<LocalDateTime> handler) throws SQLException {
    handler = getHandler(LocalDateTime.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    Timestamp value = extract(resultSet -> resultSet.getTimestamp(columnIndex));
    return null != value ? value.toLocalDateTime() : null;
  }

  public LocalDateTime getLocalDateTime(String columnLabel, TypeGetHandler<LocalDateTime> handler) throws SQLException {
    return getLocalDateTime(resultSet.findColumn(columnLabel), handler);
  }

  public LocalDateTime getLocalDateTime(int columnIndex) throws SQLException {
    return getLocalDateTime(columnIndex, null);
  }

  public LocalDateTime getLocalDateTime(String columnLabel) throws SQLException {
    return getLocalDateTime(resultSet.findColumn(columnLabel), null);
  }

  private OffsetDateTime timestampToOffsetDateTime(Timestamp value) {
    ZoneId systemZoneId = ZoneId.systemDefault();
    ZoneOffset systemZoneOffset = systemZoneId.getRules().getOffset(value.toInstant());
    return value.toInstant().atOffset(systemZoneOffset);
  }

  public OffsetDateTime getOffsetDateTime(int columnIndex, TypeGetHandler<OffsetDateTime> handler) throws SQLException {
    handler = getHandler(OffsetDateTime.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    Timestamp value = extract(resultSet -> resultSet.getTimestamp(columnIndex));
    return null != value ? timestampToOffsetDateTime(value) : null;
  }

  public OffsetDateTime getOffsetDateTime(String columnLabel, TypeGetHandler<OffsetDateTime> handler) throws SQLException {
    return getOffsetDateTime(resultSet.findColumn(columnLabel), handler);
  }

  public OffsetDateTime getOffsetDateTime(int columnIndex) throws SQLException {
    return getOffsetDateTime(columnIndex, null);
  }

  public OffsetDateTime getOffsetDateTime(String columnLabel) throws SQLException {
    return getOffsetDateTime(resultSet.findColumn(columnLabel), null);
  }

  public Array getArray(int columnIndex, TypeGetHandler<Array> handler) throws SQLException {
    handler = getHandler(Array.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getArray);
  }

  public Array getArray(String columnLabel, TypeGetHandler<Array> handler) throws SQLException {
    return getArray(resultSet.findColumn(columnLabel), handler);
  }

  public Array getArray(int columnIndex) throws SQLException {
    return getArray(columnIndex, null);
  }

  public Array getArray(String columnLabel) throws SQLException {
    return getArray(resultSet.findColumn(columnLabel), null);
  }

  public InputStream getAsciiStream(int columnIndex, TypeGetHandler<InputStream> handler) throws SQLException {
    handler = getHandler(InputStream.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getAsciiStream);
  }

  public InputStream getAsciiStream(String columnLabel, TypeGetHandler<InputStream> handler) throws SQLException {
    return getAsciiStream(resultSet.findColumn(columnLabel), handler);
  }

  public InputStream getAsciiStream(int columnIndex) throws SQLException {
    return getAsciiStream(columnIndex, null);
  }

  public InputStream getAsciiStream(String columnLabel) throws SQLException {
    return getAsciiStream(resultSet.findColumn(columnLabel), null);
  }

  public BigDecimal getBigDecimal(int columnIndex, TypeGetHandler<BigDecimal> handler) throws SQLException {
    handler = getHandler(BigDecimal.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getBigDecimal);
  }

  public BigDecimal getBigDecimal(String columnLabel, TypeGetHandler<BigDecimal> handler) throws SQLException {
    return getBigDecimal(resultSet.findColumn(columnLabel), handler);
  }

  public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
    return getBigDecimal(columnIndex, null);
  }

  public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
    return getBigDecimal(resultSet.findColumn(columnLabel), null);
  }

  public InputStream getBinaryStream(int columnIndex, TypeGetHandler<InputStream> handler) throws SQLException {
    handler = getHandler(InputStream.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getBinaryStream);
  }

  public InputStream getBinaryStream(String columnLabel, TypeGetHandler<InputStream> handler) throws SQLException {
    return getBinaryStream(resultSet.findColumn(columnLabel), handler);
  }

  public InputStream getBinaryStream(int columnIndex) throws SQLException {
    return getBinaryStream(columnIndex, null);
  }

  public InputStream getBinaryStream(String columnLabel) throws SQLException {
    return getBinaryStream(resultSet.findColumn(columnLabel), null);
  }

  public Blob getBlob(int columnIndex, TypeGetHandler<Blob> handler) throws SQLException {
    handler = getHandler(Blob.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getBlob);
  }

  public Blob getBlob(String columnLabel, TypeGetHandler<Blob> handler) throws SQLException {
    return getBlob(resultSet.findColumn(columnLabel), handler);
  }

  public Blob getBlob(int columnIndex) throws SQLException {
    return getBlob(columnIndex, null);
  }

  public Blob getBlob(String columnLabel) throws SQLException {
    return getBlob(resultSet.findColumn(columnLabel), null);
  }

  public Boolean getBoolean(int columnIndex, TypeGetHandler<Boolean> handler) throws SQLException {
    handler = getHandler(Boolean.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getBoolean);
  }

  public Boolean getBoolean(String columnLabel, TypeGetHandler<Boolean> handler) throws SQLException {
    return getBoolean(resultSet.findColumn(columnLabel), handler);
  }

  public Boolean getBoolean(int columnIndex) throws SQLException {
    return getBoolean(columnIndex, null);
  }

  public Boolean getBoolean(String columnLabel) throws SQLException {
    return getBoolean(resultSet.findColumn(columnLabel), null);
  }

  public Byte getByte(int columnIndex, TypeGetHandler<Byte> handler) throws SQLException {
    handler = getHandler(Byte.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getByte);
  }

  public Byte getByte(String columnLabel, TypeGetHandler<Byte> handler) throws SQLException {
    return getByte(resultSet.findColumn(columnLabel), handler);
  }

  public Byte getByte(int columnIndex) throws SQLException {
    return getByte(columnIndex, null);
  }

  public Byte getByte(String columnLabel) throws SQLException {
    return getByte(resultSet.findColumn(columnLabel), null);
  }

  public byte[] getBytes(int columnIndex, TypeGetHandler<byte[]> handler) throws SQLException {
    handler = getHandler(byte[].class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getBytes);
  }

  public byte[] getBytes(String columnLabel, TypeGetHandler<byte[]> handler) throws SQLException {
    return getBytes(resultSet.findColumn(columnLabel), handler);
  }

  public byte[] getBytes(int columnIndex) throws SQLException {
    return getBytes(columnIndex, null);
  }

  public byte[] getBytes(String columnLabel) throws SQLException {
    return getBytes(resultSet.findColumn(columnLabel), null);
  }

  public Reader getCharacterStream(int columnIndex, TypeGetHandler<Reader> handler) throws SQLException {
    handler = getHandler(Reader.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getCharacterStream);
  }

  public Reader getCharacterStream(String columnLabel, TypeGetHandler<Reader> handler) throws SQLException {
    return getCharacterStream(resultSet.findColumn(columnLabel), handler);
  }

  public Reader getCharacterStream(int columnIndex) throws SQLException {
    return getCharacterStream(columnIndex, null);
  }

  public Reader getCharacterStream(String columnLabel) throws SQLException {
    return getCharacterStream(resultSet.findColumn(columnLabel), null);
  }

  public Clob getClob(int columnIndex, TypeGetHandler<Clob> handler) throws SQLException {
    handler = getHandler(Clob.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getClob);
  }

  public Clob getClob(String columnLabel, TypeGetHandler<Clob> handler) throws SQLException {
    return getClob(resultSet.findColumn(columnLabel), handler);
  }

  public Clob getClob(int columnIndex) throws SQLException {
    return getClob(columnIndex, null);
  }

  public Clob getClob(String columnLabel) throws SQLException {
    return getClob(resultSet.findColumn(columnLabel), null);
  }

  public Date getDate(int columnIndex, TypeGetHandler<Date> handler) throws SQLException {
    handler = getHandler(Date.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getDate);
  }

  public Date getDate(String columnLabel, TypeGetHandler<Date> handler) throws SQLException {
    return getDate(resultSet.findColumn(columnLabel), handler);
  }

  public Date getDate(int columnIndex) throws SQLException {
    return getDate(columnIndex, null);
  }

  public Date getDate(String columnLabel) throws SQLException {
    return getDate(resultSet.findColumn(columnLabel), null);
  }

  public Double getDouble(int columnIndex, TypeGetHandler<Double> handler) throws SQLException {
    handler = getHandler(Double.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getDouble);
  }

  public Double getDouble(String columnLabel, TypeGetHandler<Double> handler) throws SQLException {
    return getDouble(resultSet.findColumn(columnLabel), handler);
  }

  public Double getDouble(int columnIndex) throws SQLException {
    return getDouble(columnIndex, null);
  }

  public Double getDouble(String columnLabel) throws SQLException {
    return getDouble(resultSet.findColumn(columnLabel), null);
  }

  public Float getFloat(int columnIndex, TypeGetHandler<Float> handler) throws SQLException {
    handler = getHandler(Float.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getFloat);
  }

  public Float getFloat(String columnLabel, TypeGetHandler<Float> handler) throws SQLException {
    return getFloat(resultSet.findColumn(columnLabel), handler);
  }

  public Float getFloat(int columnIndex) throws SQLException {
    return getFloat(columnIndex, null);
  }

  public Float getFloat(String columnLabel) throws SQLException {
    return getFloat(resultSet.findColumn(columnLabel), null);
  }

  public Integer getInt(int columnIndex, TypeGetHandler<Integer> handler) throws SQLException {
    handler = getHandler(Integer.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getInt);
  }

  public Integer getInt(String columnLabel, TypeGetHandler<Integer> handler) throws SQLException {
    return getInt(resultSet.findColumn(columnLabel), handler);
  }

  public Integer getInt(int columnIndex) throws SQLException {
    return getInt(columnIndex, null);
  }

  public Integer getInt(String columnLabel) throws SQLException {
    return getInt(resultSet.findColumn(columnLabel), null);
  }

  public Long getLong(int columnIndex, TypeGetHandler<Long> handler) throws SQLException {
    handler = getHandler(Long.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getLong);
  }

  public Long getLong(String columnLabel, TypeGetHandler<Long> handler) throws SQLException {
    return getLong(resultSet.findColumn(columnLabel), handler);
  }

  public Long getLong(int columnIndex) throws SQLException {
    return getLong(columnIndex, null);
  }

  public Long getLong(String columnLabel) throws SQLException {
    return getLong(resultSet.findColumn(columnLabel), null);
  }

  public Reader getNCharacterStream(int columnIndex, TypeGetHandler<Reader> handler) throws SQLException {
    handler = getHandler(Reader.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getNCharacterStream);
  }

  public Reader getNCharacterStream(String columnLabel, TypeGetHandler<Reader> handler) throws SQLException {
    return getNCharacterStream(resultSet.findColumn(columnLabel), handler);
  }

  public Reader getNCharacterStream(int columnIndex) throws SQLException {
    return getNCharacterStream(columnIndex, null);
  }

  public Reader getNCharacterStream(String columnLabel) throws SQLException {
    return getNCharacterStream(resultSet.findColumn(columnLabel), null);
  }

  public NClob getNClob(int columnIndex, TypeGetHandler<NClob> handler) throws SQLException {
    handler = getHandler(NClob.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getNClob);
  }

  public NClob getNClob(String columnLabel, TypeGetHandler<NClob> handler) throws SQLException {
    return getNClob(resultSet.findColumn(columnLabel), handler);
  }

  public NClob getNClob(int columnIndex) throws SQLException {
    return getNClob(columnIndex, null);
  }

  public NClob getNClob(String columnLabel) throws SQLException {
    return getNClob(resultSet.findColumn(columnLabel), null);
  }

  public String getNString(int columnIndex, TypeGetHandler<String> handler) throws SQLException {
    handler = getHandler(String.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getNString);
  }

  public String getNString(String columnLabel, TypeGetHandler<String> handler) throws SQLException {
    return getNString(resultSet.findColumn(columnLabel), handler);
  }

  public String getNString(int columnIndex) throws SQLException {
    return getNString(columnIndex, null);
  }

  public String getNString(String columnLabel) throws SQLException {
    return getNString(resultSet.findColumn(columnLabel), null);
  }

  public Ref getRef(int columnIndex, TypeGetHandler<Ref> handler) throws SQLException {
    handler = getHandler(Ref.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getRef);
  }

  public Ref getRef(String columnLabel, TypeGetHandler<Ref> handler) throws SQLException {
    return getRef(resultSet.findColumn(columnLabel), handler);
  }

  public Ref getRef(int columnIndex) throws SQLException {
    return getRef(columnIndex, null);
  }

  public Ref getRef(String columnLabel) throws SQLException {
    return getRef(resultSet.findColumn(columnLabel), null);
  }

  public RowId getRowId(int columnIndex, TypeGetHandler<RowId> handler) throws SQLException {
    handler = getHandler(RowId.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getRowId);
  }

  public RowId getRowId(String columnLabel, TypeGetHandler<RowId> handler) throws SQLException {
    return getRowId(resultSet.findColumn(columnLabel), handler);
  }

  public RowId getRowId(int columnIndex) throws SQLException {
    return getRowId(columnIndex, null);
  }

  public RowId getRowId(String columnLabel) throws SQLException {
    return getRowId(resultSet.findColumn(columnLabel), null);
  }

  public SQLXML getSQLXML(int columnIndex, TypeGetHandler<SQLXML> handler) throws SQLException {
    handler = getHandler(SQLXML.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getSQLXML);
  }

  public SQLXML getSQLXML(String columnLabel, TypeGetHandler<SQLXML> handler) throws SQLException {
    return getSQLXML(resultSet.findColumn(columnLabel), handler);
  }

  public SQLXML getSQLXML(int columnIndex) throws SQLException {
    return getSQLXML(columnIndex, null);
  }

  public SQLXML getSQLXML(String columnLabel) throws SQLException {
    return getSQLXML(resultSet.findColumn(columnLabel), null);
  }

  public Short getShort(int columnIndex, TypeGetHandler<Short> handler) throws SQLException {
    handler = getHandler(Short.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getShort);
  }

  public Short getShort(String columnLabel, TypeGetHandler<Short> handler) throws SQLException {
    return getShort(resultSet.findColumn(columnLabel), handler);
  }

  public Short getShort(int columnIndex) throws SQLException {
    return getShort(columnIndex, null);
  }

  public Short getShort(String columnLabel) throws SQLException {
    return getShort(resultSet.findColumn(columnLabel), null);
  }

  public String getString(int columnIndex, TypeGetHandler<String> handler) throws SQLException {
    handler = getHandler(String.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getString);
  }

  public String getString(String columnLabel, TypeGetHandler<String> handler) throws SQLException {
    return getString(resultSet.findColumn(columnLabel), handler);
  }

  public String getString(int columnIndex) throws SQLException {
    return getString(columnIndex, null);
  }

  public String getString(String columnLabel) throws SQLException {
    return getString(resultSet.findColumn(columnLabel), null);
  }

  public Time getTime(int columnIndex, TypeGetHandler<Time> handler) throws SQLException {
    handler = getHandler(Time.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getTime);
  }

  public Time getTime(String columnLabel, TypeGetHandler<Time> handler) throws SQLException {
    return getTime(resultSet.findColumn(columnLabel), handler);
  }

  public Time getTime(int columnIndex) throws SQLException {
    return getTime(columnIndex, null);
  }

  public Time getTime(String columnLabel) throws SQLException {
    return getTime(resultSet.findColumn(columnLabel), null);
  }

  public Timestamp getTimestamp(int columnIndex, TypeGetHandler<Timestamp> handler) throws SQLException {
    handler = getHandler(Timestamp.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getTimestamp);
  }

  public Timestamp getTimestamp(String columnLabel, TypeGetHandler<Timestamp> handler) throws SQLException {
    return getTimestamp(resultSet.findColumn(columnLabel), handler);
  }

  public Timestamp getTimestamp(int columnIndex) throws SQLException {
    return getTimestamp(columnIndex, null);
  }

  public Timestamp getTimestamp(String columnLabel) throws SQLException {
    return getTimestamp(resultSet.findColumn(columnLabel), null);
  }

  public URL getURL(int columnIndex, TypeGetHandler<URL> handler) throws SQLException {
    handler = getHandler(URL.class, handler);
    if (null != handler) {
      return handler.doGet(resultSet, columnIndex);
    }
    return extract(resultSet, columnIndex, resultSet::getURL);
  }

  public URL getURL(String columnLabel, TypeGetHandler<URL> handler) throws SQLException {
    return getURL(resultSet.findColumn(columnLabel), handler);
  }

  public URL getURL(int columnIndex) throws SQLException {
    return getURL(columnIndex, null);
  }

  public URL getURL(String columnLabel) throws SQLException {
    return getURL(resultSet.findColumn(columnLabel), null);
  }
}

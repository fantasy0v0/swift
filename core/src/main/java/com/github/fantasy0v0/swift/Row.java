package com.github.fantasy0v0.swift;

import com.github.fantasy0v0.swift.parameter.ParameterGetter;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.*;

public class Row {

  private final ResultSet resultSet;

  private final Map<Class<?>, ParameterGetter<?>> getterMap;

  Row(ResultSet resultSet, Map<Class<?>, ParameterGetter<?>> getterMap) {
    this.resultSet = resultSet;
    this.getterMap = getterMap;
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

  private <P> P extract(int columnIndex, ResultSetExtractFunction<P> function) throws SQLException {
    P value = function.apply(columnIndex);
    return resultSet.wasNull() ? null : value;
  }

  @SuppressWarnings("unchecked")
  private <T> ParameterGetter<T> getGetter(Class<T> type) {
    if (null == getterMap || getterMap.isEmpty()) {
      return null;
    }
    return (ParameterGetter<T>)getterMap.get(type);
  }

  public Object getObject(int columnIndex) throws SQLException {
    return extract(resultSet -> resultSet.getObject(columnIndex));
  }

  public Object getObject(String columnLabel) throws SQLException {
    return extract(resultSet -> resultSet.getObject(columnLabel));
  }

  private <T> T getByGetter(ResultSet resultSet, ParameterGetter<T> getter, int columnIndex) throws SQLException {
    Object parameter = resultSet.getObject(columnIndex);
    return getter.get(resultSet.getMetaData(), columnIndex, resultSet.wasNull() ? null : parameter);
  }

  private <T> T getByGetter(ParameterGetter<T> getter, int columnIndex) throws SQLException {
    return getByGetter(resultSet, getter, columnIndex);
  }

  public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
    ParameterGetter<T> getter = getGetter(type);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(resultSet -> resultSet.getObject(columnIndex, type));
  }

  public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
    return getObject(resultSet.findColumn(columnLabel), type);
  }

  public <T> T getByFunction(FunctionWithException<T> function) throws SQLException {
    if (null == function) {
      throw new IllegalArgumentException("function cannot be null");
    }
    return function.apply(resultSet);
  }

  public LocalTime getLocalTime(int columnIndex) throws SQLException {
    return getObject(columnIndex, LocalTime.class);
  }

  public LocalTime getLocalTime(String columnLabel) throws SQLException {
    return getLocalTime(resultSet.findColumn(columnLabel));
  }

  public LocalDate getLocalDate(int columnIndex) throws SQLException {
    return getObject(columnIndex, LocalDate.class);
  }

  public LocalDate getLocalDate(String columnLabel) throws SQLException {
    return getLocalDate(resultSet.findColumn(columnLabel));
  }

  public LocalDateTime getLocalDateTime(int columnIndex) throws SQLException {
    return getObject(columnIndex, LocalDateTime.class);
  }

  public LocalDateTime getLocalDateTime(String columnLabel) throws SQLException {
    return getLocalDateTime(resultSet.findColumn(columnLabel));
  }

  public OffsetDateTime getOffsetDateTime(int columnIndex) throws SQLException {
    return getObject(columnIndex, OffsetDateTime.class);
  }

  public OffsetDateTime getOffsetDateTime(String columnLabel) throws SQLException {
    return getOffsetDateTime(resultSet.findColumn(columnLabel));
  }

  @SuppressWarnings("unchecked")
  public <T> List<T> getArray(int columnIndex, Class<T> type) throws SQLException {
    Array array = getArray(columnIndex);
    if (null == array) {
      return Collections.emptyList();
    }
    ParameterGetter<T> getter = getGetter(type);
    try (ResultSet arrayResultSet = array.getResultSet()) {
      // ResultSet 的第一列是元素索引，第二列是元素值
      List<T> list = new ArrayList<>();
      while (arrayResultSet.next()) {
        if (null != getter) {
          Object parameter = arrayResultSet.getObject(2);
          list.add(getter.get(arrayResultSet.getMetaData(), 2, arrayResultSet.wasNull() ? null : parameter));
        } else {
          T parameter = arrayResultSet.getObject(2, type);
          list.add(arrayResultSet.wasNull() ? null : parameter);
        }
      }
      return list;
    } finally {
      array.free();
    }
  }

  /**
   * 将当前行数据转为数组
   *
   * @return Object[]
   * @throws SQLException 获取数据失败
   */
  public Object[] toArray() throws SQLException {
    int columnCount = getColumnCount();
    Object[] row = new Object[columnCount];
    for (int i = 0; i < columnCount; i++) {
      row[i] = getObject(i + 1);
    }
    return row;
  }

  /**
   * 将当前行数据转为Map
   *
   * @return Map<String, Object>
   * @throws SQLException 获取数据失败
   */
  public Map<String, Object> toMap() throws SQLException {
    int columnCount = getColumnCount();
    Map<String, Object> row = new HashMap<>();
    for (int i = 0; i < columnCount; i++) {
      String columnLabel = getColumnLabel(i + 1);
      row.put(columnLabel, getObject(i + 1));
    }
    return Collections.unmodifiableMap(row);
  }

  // 以下均为自动生成的代码
  // 如需修改请到RowGenerateTest类中进行修改

  public Array getArray(int columnIndex) throws SQLException {
    ParameterGetter<Array> getter = getGetter(Array.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getArray);
  }

  public Array getArray(String columnLabel) throws SQLException {
    return getArray(resultSet.findColumn(columnLabel));
  }

  public InputStream getAsciiStream(int columnIndex) throws SQLException {
    ParameterGetter<InputStream> getter = getGetter(InputStream.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getAsciiStream);
  }

  public InputStream getAsciiStream(String columnLabel) throws SQLException {
    return getAsciiStream(resultSet.findColumn(columnLabel));
  }

  public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
    ParameterGetter<BigDecimal> getter = getGetter(BigDecimal.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getBigDecimal);
  }

  public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
    return getBigDecimal(resultSet.findColumn(columnLabel));
  }

  public InputStream getBinaryStream(int columnIndex) throws SQLException {
    ParameterGetter<InputStream> getter = getGetter(InputStream.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getBinaryStream);
  }

  public InputStream getBinaryStream(String columnLabel) throws SQLException {
    return getBinaryStream(resultSet.findColumn(columnLabel));
  }

  public Blob getBlob(int columnIndex) throws SQLException {
    ParameterGetter<Blob> getter = getGetter(Blob.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getBlob);
  }

  public Blob getBlob(String columnLabel) throws SQLException {
    return getBlob(resultSet.findColumn(columnLabel));
  }

  public Boolean getBoolean(int columnIndex) throws SQLException {
    ParameterGetter<Boolean> getter = getGetter(Boolean.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getBoolean);
  }

  public Boolean getBoolean(String columnLabel) throws SQLException {
    return getBoolean(resultSet.findColumn(columnLabel));
  }

  public Byte getByte(int columnIndex) throws SQLException {
    ParameterGetter<Byte> getter = getGetter(Byte.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getByte);
  }

  public Byte getByte(String columnLabel) throws SQLException {
    return getByte(resultSet.findColumn(columnLabel));
  }

  public byte[] getBytes(int columnIndex) throws SQLException {
    ParameterGetter<byte[]> getter = getGetter(byte[].class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getBytes);
  }

  public byte[] getBytes(String columnLabel) throws SQLException {
    return getBytes(resultSet.findColumn(columnLabel));
  }

  public Reader getCharacterStream(int columnIndex) throws SQLException {
    ParameterGetter<Reader> getter = getGetter(Reader.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getCharacterStream);
  }

  public Reader getCharacterStream(String columnLabel) throws SQLException {
    return getCharacterStream(resultSet.findColumn(columnLabel));
  }

  public Clob getClob(int columnIndex) throws SQLException {
    ParameterGetter<Clob> getter = getGetter(Clob.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getClob);
  }

  public Clob getClob(String columnLabel) throws SQLException {
    return getClob(resultSet.findColumn(columnLabel));
  }

  public Date getDate(int columnIndex) throws SQLException {
    ParameterGetter<Date> getter = getGetter(Date.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getDate);
  }

  public Date getDate(String columnLabel) throws SQLException {
    return getDate(resultSet.findColumn(columnLabel));
  }

  public Double getDouble(int columnIndex) throws SQLException {
    ParameterGetter<Double> getter = getGetter(Double.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getDouble);
  }

  public Double getDouble(String columnLabel) throws SQLException {
    return getDouble(resultSet.findColumn(columnLabel));
  }

  public Float getFloat(int columnIndex) throws SQLException {
    ParameterGetter<Float> getter = getGetter(Float.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getFloat);
  }

  public Float getFloat(String columnLabel) throws SQLException {
    return getFloat(resultSet.findColumn(columnLabel));
  }

  public Integer getInt(int columnIndex) throws SQLException {
    ParameterGetter<Integer> getter = getGetter(Integer.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getInt);
  }

  public Integer getInt(String columnLabel) throws SQLException {
    return getInt(resultSet.findColumn(columnLabel));
  }

  public Long getLong(int columnIndex) throws SQLException {
    ParameterGetter<Long> getter = getGetter(Long.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getLong);
  }

  public Long getLong(String columnLabel) throws SQLException {
    return getLong(resultSet.findColumn(columnLabel));
  }

  public Reader getNCharacterStream(int columnIndex) throws SQLException {
    ParameterGetter<Reader> getter = getGetter(Reader.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getNCharacterStream);
  }

  public Reader getNCharacterStream(String columnLabel) throws SQLException {
    return getNCharacterStream(resultSet.findColumn(columnLabel));
  }

  public NClob getNClob(int columnIndex) throws SQLException {
    ParameterGetter<NClob> getter = getGetter(NClob.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getNClob);
  }

  public NClob getNClob(String columnLabel) throws SQLException {
    return getNClob(resultSet.findColumn(columnLabel));
  }

  public String getNString(int columnIndex) throws SQLException {
    ParameterGetter<String> getter = getGetter(String.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getNString);
  }

  public String getNString(String columnLabel) throws SQLException {
    return getNString(resultSet.findColumn(columnLabel));
  }

  public Ref getRef(int columnIndex) throws SQLException {
    ParameterGetter<Ref> getter = getGetter(Ref.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getRef);
  }

  public Ref getRef(String columnLabel) throws SQLException {
    return getRef(resultSet.findColumn(columnLabel));
  }

  public RowId getRowId(int columnIndex) throws SQLException {
    ParameterGetter<RowId> getter = getGetter(RowId.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getRowId);
  }

  public RowId getRowId(String columnLabel) throws SQLException {
    return getRowId(resultSet.findColumn(columnLabel));
  }

  public SQLXML getSQLXML(int columnIndex) throws SQLException {
    ParameterGetter<SQLXML> getter = getGetter(SQLXML.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getSQLXML);
  }

  public SQLXML getSQLXML(String columnLabel) throws SQLException {
    return getSQLXML(resultSet.findColumn(columnLabel));
  }

  public Short getShort(int columnIndex) throws SQLException {
    ParameterGetter<Short> getter = getGetter(Short.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getShort);
  }

  public Short getShort(String columnLabel) throws SQLException {
    return getShort(resultSet.findColumn(columnLabel));
  }

  public String getString(int columnIndex) throws SQLException {
    ParameterGetter<String> getter = getGetter(String.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getString);
  }

  public String getString(String columnLabel) throws SQLException {
    return getString(resultSet.findColumn(columnLabel));
  }

  public Time getTime(int columnIndex) throws SQLException {
    ParameterGetter<Time> getter = getGetter(Time.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getTime);
  }

  public Time getTime(String columnLabel) throws SQLException {
    return getTime(resultSet.findColumn(columnLabel));
  }

  public Timestamp getTimestamp(int columnIndex) throws SQLException {
    ParameterGetter<Timestamp> getter = getGetter(Timestamp.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getTimestamp);
  }

  public Timestamp getTimestamp(String columnLabel) throws SQLException {
    return getTimestamp(resultSet.findColumn(columnLabel));
  }

  public URL getURL(int columnIndex) throws SQLException {
    ParameterGetter<URL> getter = getGetter(URL.class);
    if (null != getter) {
      return getByGetter(getter, columnIndex);
    }
    return extract(columnIndex, resultSet::getURL);
  }

  public URL getURL(String columnLabel) throws SQLException {
    return getURL(resultSet.findColumn(columnLabel));
  }
}

package com.github.fantasy0v0.swift.jdbc.parameter;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @author fan 2025/9/30
 */
public class ParameterMetaData {

  private final ResultSetMetaData metaData;

  private final int columnIndex;

  public ParameterMetaData(ResultSetMetaData metaData, int columnIndex) {
    this.metaData = metaData;
    this.columnIndex = columnIndex;
  }

  /**
   * 当现有方法无法满足时, 可以使用该方法代替
   *
   * @return ResultSetMetaData
   */
  public ResultSetMetaData metaData() {
    return metaData;
  }

  /**
   * 当现有方法无法满足时, 可以使用该方法代替
   *
   * @return column index
   */
  public int columnIndex() {
    return columnIndex;
  }

  public int columnType() throws SQLException {
    return metaData.getColumnType(columnIndex);
  }

  public String columnTypeName() throws SQLException {
    return metaData.getColumnTypeName(columnIndex);
  }

  public int precision() throws SQLException {
    return metaData.getPrecision(columnIndex);
  }

  public int scale() throws SQLException {
    return metaData.getScale(columnIndex);
  }

}

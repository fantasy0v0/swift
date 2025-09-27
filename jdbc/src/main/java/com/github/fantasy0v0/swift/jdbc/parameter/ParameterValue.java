package com.github.fantasy0v0.swift.jdbc.parameter;

/**
 * 存储转换后的参数信息
 * 因为有SQLType的存在, 这里无法写成record
 *
 * @author fan 2025/9/27
 */
public class ParameterValue {

  private final Object parameter;

  private final int targetSqlType;

  private final Integer scaleOrLength;


  public ParameterValue(Object parameter, int targetSqlType, Integer scaleOrLength) {
    this.parameter = parameter;
    this.targetSqlType = targetSqlType;
    this.scaleOrLength = scaleOrLength;
  }

  public Object getParameter() {
    return parameter;
  }

  public int getTargetSqlType() {
    return targetSqlType;
  }

  public Integer getScaleOrLength() {
    return scaleOrLength;
  }
}

package test;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

class RowTest {

  private final Logger log = LoggerFactory.getLogger(RowTest.class);

  @Test
  void generateMethods() {
    Set<String> packages = new HashSet<>();
    Map<String, String> primitiveTypeMap = getPrimitiveTypeMap();

    StringBuilder methodsBuffer = new StringBuilder();
    Method[] methods = ResultSet.class.getDeclaredMethods();
    Arrays.sort(methods, Comparator.comparing(Method::getName));
    for (Method method : methods) {
      if (!method.getName().startsWith("get")) {
        continue;
      }
      Deprecated annotation = method.getAnnotation(Deprecated.class);
      if (null != annotation) {
        continue;
      }
      int parameterCount = method.getParameterCount();
      if (parameterCount != 1) {
        continue;
      }
      Class<?> parameterType = method.getParameterTypes()[0];
      if (!parameterType.equals(int.class)) {
        continue;
      }

      String resultType = getResultType(packages, primitiveTypeMap, method);
      String methodName = method.getName();

      methodsBuffer.append(System.lineSeparator());
      methodsBuffer.append(System.lineSeparator());
      methodsBuffer.append("""
      public %s %s(int columnIndex) throws SQLException {
        return extract(resultSet -> resultSet.%s(columnIndex));
      }""".formatted(resultType, methodName, methodName));
      methodsBuffer.append(System.lineSeparator());
      methodsBuffer.append(System.lineSeparator());
      methodsBuffer.append("""
      public %s %s(String columnLabel) throws SQLException {
        return extract(resultSet -> resultSet.%s(columnLabel));
      }""".formatted(resultType, methodName, methodName));
    }
    methodsBuffer.append(System.lineSeparator());
    methodsBuffer.append(System.lineSeparator());
    methodsBuffer.append("""
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

      public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        return extract(resultSet -> resultSet.getObject(columnIndex, type));
      }

      public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return extract(resultSet -> resultSet.getObject(columnLabel, type));
      }""");
    addPackages(packages, LocalTime.class);
    addPackages(packages, LocalDate.class);
    addPackages(packages, LocalDateTime.class);
    StringBuilder classBuffer = new StringBuilder();
    classBuffer.append(System.lineSeparator());
    classBuffer.append("package com.github.fantasy0v0.swift.jdbc;");
    classBuffer.append(System.lineSeparator());
    for (String pkg : packages) {
      classBuffer.append(System.lineSeparator());
      classBuffer.append("import %s;".formatted(pkg));
    }
    classBuffer.append(System.lineSeparator());
    classBuffer.append(System.lineSeparator());
    classBuffer.append("""
      public class Row {

        private final ResultSet resultSet;

        Row(ResultSet resultSet) {
          this.resultSet = resultSet;
        }""");
    classBuffer.append(System.lineSeparator());
    classBuffer.append(methodsBuffer);
    classBuffer.append(System.lineSeparator());
    classBuffer.append(System.lineSeparator());
    classBuffer.append("""
      }
      """);
    log.info("class: {}", classBuffer);
  }

  private static Map<String, String> getPrimitiveTypeMap() {
    Map<String, String> primitiveTypeMap = new HashMap<>();
    primitiveTypeMap.put("boolean", Boolean.class.getSimpleName());
    primitiveTypeMap.put("byte", Byte.class.getSimpleName());
    primitiveTypeMap.put("short", Short.class.getSimpleName());
    primitiveTypeMap.put("int", Integer.class.getSimpleName());
    primitiveTypeMap.put("long", Long.class.getSimpleName());
    primitiveTypeMap.put("float", Float.class.getSimpleName());
    primitiveTypeMap.put("double", Double.class.getSimpleName());
    // typeMap.put("byte[]", Byte[].class.getSimpleName());
    return primitiveTypeMap;
  }

  private void addPackages(Set<String> packages, Class<?> type) {
    String packageName = type.getName();
    if (!type.isPrimitive() && !packageName.startsWith("java.lang")) {
      packages.add(packageName);
    }
  }

  private String getResultType(Set<String> packages,
                               Map<String, String> typeMap,
                               Method method) {
    Class<?> returnType = method.getReturnType();
    if (!returnType.isPrimitive()) {
      if (returnType.isArray()) {
        Class<?> componentType = returnType.getComponentType();
        addPackages(packages, componentType);
        String typeName = componentType.getSimpleName();
        return "%s[]".formatted(typeName);
      } else {
        addPackages(packages, returnType);
        return returnType.getSimpleName();
      }
    } else {
      String simpleName = typeMap.get(returnType.getName());
      if (null == simpleName) {
        throw new RuntimeException("尚未支持 %s".formatted(returnType.getName()));
      }
      return simpleName;
    }
  }

}

package test;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.*;

class RowGenerateTest {

  private final Logger log = LoggerFactory.getLogger(RowGenerateTest.class);

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
      if (method.getName().equals("getObject")) {
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
        public %s %s(int columnIndex, TypeGetHandler<%s> handler) throws SQLException {
          handler = getHandler(%s.class, handler);
          if (null != handler) {
            return handler.doGet(resultSet, columnIndex);
          }
          return extract(resultSet, columnIndex, resultSet::%s);
        }""".formatted(resultType, methodName, resultType, resultType, methodName));

      methodsBuffer.append(System.lineSeparator());
      methodsBuffer.append(System.lineSeparator());
      methodsBuffer.append("""
        public %s %s(String columnLabel, TypeGetHandler<%s> handler) throws SQLException {
          return %s(resultSet.findColumn(columnLabel), handler);
        }""".formatted(resultType, methodName, resultType, methodName));

      methodsBuffer.append(System.lineSeparator());
      methodsBuffer.append(System.lineSeparator());
      methodsBuffer.append("""
        public %s %s(int columnIndex) throws SQLException {
          return %s(columnIndex, null);
        }""".formatted(resultType, methodName, methodName));

      methodsBuffer.append(System.lineSeparator());
      methodsBuffer.append(System.lineSeparator());
      methodsBuffer.append("""
        public %s %s(String columnLabel) throws SQLException {
          return %s(resultSet.findColumn(columnLabel), null);
        }""".formatted(resultType, methodName, methodName));
    }
    methodsBuffer.append(System.lineSeparator());
    methodsBuffer.append(System.lineSeparator());
    addPackages(packages, LocalTime.class);
    addPackages(packages, LocalDate.class);
    addPackages(packages, LocalDateTime.class);
    addPackages(packages, OffsetDateTime.class);
    StringBuilder classBuffer = new StringBuilder();
    classBuffer.append(System.lineSeparator());
    for (String pkg : packages) {
      classBuffer.append(System.lineSeparator());
      classBuffer.append("import %s;".formatted(pkg));
    }
    classBuffer.append(System.lineSeparator());
    classBuffer.append(System.lineSeparator());
    classBuffer.append(methodsBuffer);
    classBuffer.append(System.lineSeparator());
    classBuffer.append(System.lineSeparator());
    log.info("{}", classBuffer);
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

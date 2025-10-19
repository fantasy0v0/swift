package test.jdbc;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.Allowed;
import test.container.Db;
import test.container.SwiftJdbcExtension;

import javax.sql.DataSource;
import java.sql.*;

@ExtendWith(SwiftJdbcExtension.class)
public class ArrayTest {

  private final static Logger log = LoggerFactory.getLogger(ArrayTest.class);

  /**
   * 测试从列中取出数组
   *
   * @param dataSource dataSource
   * @throws SQLException SQLException
   */
  @Allowed(Db.Postgres)
  @TestTemplate
  void test(DataSource dataSource) throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      String sql = "select tags from swift_user where tags is not null";
      try (PreparedStatement ps = connection.prepareStatement(sql)) {
        try (ResultSet resultSet = ps.executeQuery()) {
          while (resultSet.next()) {
            Array array = resultSet.getArray(1);
            Object object = array.getArray();
            log.debug("array: {} baseType: {}, baseTypeName: {} getArray: {}, getArrayClass: {}",
              array, array.getBaseType(), array.getBaseTypeName(),
              object, object.getClass());
            try (var arrayResultSet = array.getResultSet()) {
              int index = 1;
              while (arrayResultSet.next()) {
                // columnIndex: 1 代表在数组中的索引
                // columnIndex: 2 代表数组中的元素
                var metaData = new com.github.fantasy0v0.swift.jdbc.parameter.ParameterMetaData(
                  arrayResultSet.getMetaData(), 2);
                Object _object = arrayResultSet.getObject(2);
                log.debug("index: {}, type: {}, typeName: {}, object: {}",
                  index, metaData.columnType(), metaData.columnTypeName(), _object);
                index++;
              }
            }
          }
        }
      }
    }
  }

}

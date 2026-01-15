package test.connection;

import com.github.fantasy0v0.swift.ConnectionPoolUtil;
import com.github.fantasy0v0.swift.Context;
import com.github.fantasy0v0.swift.connection.ManagedConnection;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import test.container.SwiftJdbcExtension;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * @author fan 2026/1/15
 */
@ExtendWith(SwiftJdbcExtension.class)
class ManagedConnectionTest {

  @TestTemplate
  void unwrap(Context context) throws SQLException {
    // 未开启事务的情况下，
    // 如果在一个线程中调用了getConnection并且不关闭，
    // 那后续所有的getConnection获取的都会是同一个连接

    ManagedConnection connection1 = ConnectionPoolUtil.getConnection(context);
    Connection conn1 = connection1.unwrap();
    connection1.close();

    ManagedConnection connection2 = ConnectionPoolUtil.getConnection(context);
    Connection conn2 = connection2.unwrap();
    connection2.close();

    assertNotEquals(conn1, conn2);
    assertNotEquals(connection1, connection2);
  }
}

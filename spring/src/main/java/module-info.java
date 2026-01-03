import com.github.fantasy0v0.swift.connection.ConnectionPool;
import com.github.fantasy0v0.swift.spring.SpringConnectionPool;

module fantasy0v0.swift.spring {
  requires fantasy0v0.swift;
  requires spring.context;
  requires spring.jdbc;
  requires spring.tx;

  exports com.github.fantasy0v0.swift.spring;

  provides ConnectionPool with SpringConnectionPool;
}

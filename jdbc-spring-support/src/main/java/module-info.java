import com.github.fantasy0v0.swift.jdbc.connection.ConnectionPool;

module swift.jdbc.spring.support {
  requires swift.jdbc;
  requires spring.context;
  requires spring.jdbc;
  requires spring.tx;

  exports com.github.fantasy0v0.swift.jdbc.spring;

  provides ConnectionPool with com.github.fantasy0v0.swift.jdbc.spring.SpringConnectionPool;
}

module swift.jdbc.spring.support {
  requires swift.jdbc;
  requires spring.context;
  requires spring.jdbc;
  requires spring.tx;

  provides com.github.fantasy0v0.swift.jdbc.ConnectionPool with com.github.fantasy0v0.swift.jdbc.spring.SpringConnectionPool;
}

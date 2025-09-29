module swift.jdbc {
  requires transitive java.sql;
  requires transitive org.slf4j;
  requires swift.jdbc;

  exports com.github.fantasy0v0.swift.jdbc;
  exports com.github.fantasy0v0.swift.jdbc.exception;
  exports com.github.fantasy0v0.swift.jdbc.predicate;
  exports com.github.fantasy0v0.swift.jdbc.clauses;
  exports com.github.fantasy0v0.swift.jdbc.dialect;
  exports com.github.fantasy0v0.swift.jdbc.type;
  exports com.github.fantasy0v0.swift.jdbc.util;
  exports com.github.fantasy0v0.swift.jdbc.connection;

  uses com.github.fantasy0v0.swift.jdbc.connection.ConnectionPool;
}

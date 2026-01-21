import com.github.fantasy0v0.swift.connection.ManagedConnectionPool;

module fantasy0v0.swift {
  requires transitive java.sql;
  requires transitive org.slf4j;

  exports com.github.fantasy0v0.swift;
  exports com.github.fantasy0v0.swift.exception;
  exports com.github.fantasy0v0.swift.predicate;
  exports com.github.fantasy0v0.swift.clauses;
  exports com.github.fantasy0v0.swift.dialect;
  exports com.github.fantasy0v0.swift.parameter;
  exports com.github.fantasy0v0.swift.connection;

  uses ManagedConnectionPool;
}

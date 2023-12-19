module swift.jdbc {
  requires transitive java.sql;
  requires org.slf4j;
  requires org.jetbrains.annotations;

  exports com.github.fantasy0v0.swift.jdbc;
  exports com.github.fantasy0v0.swift.jdbc.exception;
  exports com.github.fantasy0v0.swift.jdbc.predicate;

}

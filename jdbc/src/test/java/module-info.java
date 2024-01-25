module swift.jdbc.test {

  requires swift.jdbc;
  requires org.junit.jupiter.api;
  requires org.slf4j;
  // requires org.slf4j.simple;
  requires com.zaxxer.hikari;
  requires org.jetbrains.annotations;

  opens test to org.junit.platform.commons;
  opens test.predicate to org.junit.platform.commons;
  opens test.handler to org.junit.platform.commons;

}

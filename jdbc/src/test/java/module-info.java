module swift.jdbc.test {

  requires swift.jdbc;
  requires org.junit.jupiter.api;
  requires org.slf4j;
  // requires org.slf4j.simple;
  requires com.zaxxer.hikari;

  opens test to org.junit.platform.commons;

}

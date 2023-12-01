module swift.jdbc.test {

  requires swift.jdbc;
  requires org.junit.jupiter.api;

  opens test to org.junit.platform.commons;

}

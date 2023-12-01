
module swift.core.test {
  requires io.helidon.webserver;
  requires io.helidon.http;
  requires io.helidon.config;
  requires jakarta.json;
  requires io.helidon.logging.common;
  requires io.helidon.webclient;
  requires swift.server;
  requires hamcrest.all;
  requires io.helidon.webserver.testing.junit5;
  requires org.graalvm.polyglot;
  requires org.slf4j;
  requires org.apache.commons.pool2;

  opens js to org.junit.platform.commons;
  opens js.test;
}

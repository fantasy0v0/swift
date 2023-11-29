
module swift.core {
  requires io.helidon.webserver;
  requires io.helidon.http;
  requires io.helidon.config;
  requires jakarta.json;
  requires io.helidon.logging.common;
  requires io.helidon.webclient;
  requires org.slf4j;

  exports com.github.fantasy0v0.swift.core;
  exports com.github.fantasy0v0.swift.core.server;
  exports com.github.fantasy0v0.swift.core.jdbc;
}

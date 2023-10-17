package com.github.fantasy0v0.swift;

import io.helidon.config.Config;
import io.helidon.logging.common.LogConfig;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;


/**
 * The application main class.
 */
public final class Main {

  /**
   * Cannot be instantiated.
   */
  private Main() {
  }

  /**
   * Application main entry point.
   *
   * @param args command line arguments.
   */
  public static void main(final String[] args) {
    // load logging configuration
    LogConfig.configureRuntime();

    // initialize global config from default configuration
    Config config = Config.create();
    Config.global(config);

    WebServer server = WebServer.builder()
      .config(Config.global().get("server"))
      .routing(Main::routing)
      .build()
      .start();

    System.out.println("WEB server is up! http://localhost:" + server.port() + "/simple-greet");
  }

  /**
   * Updates HTTP Routing.
   */
  static void routing(HttpRouting.Builder routing) {
    routing
      .get("/simple-greet", (req, res) -> res.send("Hello World!"))
      .any((req, res) -> {
        res.send("any handle");
      });
  }

}

package com.github.fantasy0v0.swift.core;

import io.helidon.config.Config;
import io.helidon.logging.common.LogConfig;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The application main class.
 */
public final class Main {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

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
  public static void main(final String[] args) throws Exception {
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

  }

  /**
   * Updates HTTP Routing.
   */
  public static void routing(HttpRouting.Builder routing) {
    routing
      .addFilter(new TestFilter())
      .any((req, res) -> {
        req.context().supply("test", String.class, () -> "what?");
        System.out.println(STR."\{Thread.currentThread()} \{req.path().path()}  1");
        res.next();
        System.out.println("1.1");
      })
      .get("/simple-greet", (req, res) -> {
        // res.send("Hello World!");
        res.header("content-type", "application/json");
        res.header("cache-control", "max-age=3600");
        res.send("""
        {
          "test": 123
        }
        """);
        System.out.println("2");
      })
      .any((req, res) -> {
        res.send("any handle");
        System.out.println(req.context().get("test", String.class));
        System.out.println(Thread.currentThread() + " " + req.path().path() + "  3");
      });
  }

}

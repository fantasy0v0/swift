package com.github.fantasy0v0.swift;

import io.helidon.config.Config;
import io.helidon.http.Header;
import io.helidon.logging.common.LogConfig;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;


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

    try(WatchService watchService = FileSystems.getDefault().newWatchService()) {
      Path path = Path.of("C:\\Users\\fan\\Desktop\\test\\2");
      path.register(watchService, ENTRY_MODIFY);
      while (true) {
        WatchKey key;
        try {
          key = watchService.take();
        } catch (InterruptedException e) {
          e.printStackTrace();
          return;
        }
        for (WatchEvent<?> event : key.pollEvents()) {
          WatchEvent.Kind<?> kind = event.kind();

          // This key is registered only
          // for ENTRY_CREATE events,
          // but an OVERFLOW event can
          // occur regardless if events
          // are lost or discarded.
          if (kind == OVERFLOW) {
            continue;
          }
          WatchEvent<Path> ev = (WatchEvent<Path>)event;
          Path filename = ev.context();
          System.out.println(filename.toString());
          // filename.register(watchService, ENTRY_MODIFY);
        }
        key.reset();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Updates HTTP Routing.
   */
  static void routing(HttpRouting.Builder routing) {
    routing
      .any((req, res) -> {
        req.context().supply("test", String.class, () -> "what?");
        System.out.println(Thread.currentThread() + " " + req.path().path() + "  1");
        res.next();
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
      })
      .any((req, res) -> {
        res.send("any handle");
        System.out.println(req.context().get("test", String.class));
        System.out.println(Thread.currentThread() + " " + req.path().path() + "  2");
      });
  }

}

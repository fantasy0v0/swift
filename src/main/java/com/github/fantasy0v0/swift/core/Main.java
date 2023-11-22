package com.github.fantasy0v0.swift.core;

import com.github.fantasy0v0.swift.core.server.SwiftServer;
import com.github.fantasy0v0.swift.core.server.SwiftServerBuilder;
import io.helidon.config.Config;
import io.helidon.logging.common.LogConfig;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import org.graalvm.polyglot.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;


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
  public static void main(final String[] args) throws IOException {
    // load logging configuration
    // LogConfig.configureRuntime();

    // initialize global config from default configuration
    /*Config config = Config.create();
    Config.global(config);*/

    /*WebServer server = WebServer.builder()
      .config(Config.global().get("server"))
      .routing(Main::routing)
      .build()
      .start();*/

    Source source = Source.newBuilder("js", """
      (function handler(req, res) {
        res.send("Hello World!");
      })
    """, null).build();

    SwiftServer server = SwiftServer.builder()
      .host("0.0.0.0")
      .port(9819)
      .routing(routing -> {
        routing.get("/simple-greet", (req, res) -> {
          Context context = createContext();
          Value handler = context.eval(source);
          try {
            handler.executeVoid(req, res);
          } catch (RuntimeException e) {
            throw e;
          }
        });
      })
      .build();

    server.start();

    // System.out.println("WEB server is up! http://localhost:" + server.port() + "/simple-greet");

    /*try(WatchService watchService = FileSystems.getDefault().newWatchService()) {
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
    }*/
  }

  /**
   * Updates HTTP Routing.
   */
  public static void routing(HttpRouting.Builder routing) {
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

  public static Context createContext() {
    return Context.newBuilder("js")
      .engine(Engine.newBuilder().build())
      .allowExperimentalOptions(true)
      .allowHostAccess(HostAccess.ALL)
      .allowHostClassLookup(clazz -> {
        log.debug("ClassLookup:{}", clazz);
        return true;
      })
      // 打印未处理的异常
      .allowExperimentalOptions(true).option("js.unhandled-rejections", "warn")
      .build();
  }

}
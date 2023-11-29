package js;

import com.github.fantasy0v0.swift.core.server.SwiftServer;
import js.factory.ContextValueFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.graalvm.polyglot.Engine;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;

import static js.JavaScriptEngineTest.createContext;

public class SwiftServerTest {

  @Test
  public void start() throws Exception {
    createContext(context -> {
      // language="js"
      Source source = Source.newBuilder("js", """
        const SwiftServer = Java.type('com.github.fantasy0v0.swift.core.server.SwiftServer');
        const server = SwiftServer.builder()
          .host("0.0.0.0")
          .port(9819)
          .routing(routing => {
             routing.get("/simple-greet", (req, res) => {
               res.send("Hello World!");
             });
           })
          .build();
        server.start();
        console.log(`WEB server is up! http://localhost:${server.port()}/simple-greet`);
        """, null).build();
      context.eval(source);
      Thread.sleep(Duration.ofDays(1));
    });
  }

  @Test
  public void withFactory() throws Exception {
    Engine engine = Engine.newBuilder().build();
    Source source = Source.newBuilder("js", """
      (function handler(req, res) {
        res.send("Hello World!");
      })
    """, null).build();

    PooledObjectFactory<Value> factory = new ContextValueFactory(engine, source);
    ObjectPool<Value> pool = new GenericObjectPool<>(factory);
    pool.addObjects(30);

    SwiftServer server = SwiftServer.builder()
      .host("0.0.0.0")
      .port(9819)
      .routing(routing -> {
        routing.get("/simple-greet", (req, res) -> {
          Value function = pool.borrowObject();
          try {
            function.executeVoid(req, res);
          } catch (RuntimeException e) {
            throw e;
          } finally {
            pool.returnObject(function);
          }
        });
      })
      .build();

    server.start();
    Thread.sleep(Duration.ofDays(1));
  }

}

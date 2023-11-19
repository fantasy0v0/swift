package js;

import org.graalvm.polyglot.Source;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static js.JavaScriptEngineTest.createContext;

public class SwiftServerTest {

  @Test
  public void start() throws Exception {
    createContext(context -> {
      context.getBindings("js").putMember("context", context);
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
          .build(context);
        server.start();
        console.log(`WEB server is up! http://localhost:${server.port()}/simple-greet`);
        """, null).build();
      context.eval(source);
      Thread.sleep(Duration.ofDays(1));
    });
  }

}

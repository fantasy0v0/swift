package js;

import org.graalvm.polyglot.Source;
import org.junit.jupiter.api.Test;
import static js.JavaScriptEngineTest.createContext;

public class SwiftServerTest {

  @Test
  public void start() {
    createContext(context -> {
      Source source = Source.newBuilder("js", """
        const SwiftServer = Java.type('com.github.fantasy0v0.swift.core.server.SwiftServer');
        const server = SwiftServer.builder()
          .host("127.0.0.1")
          .routing(routing => {
             routing.get("/simple-greet", (req, res) => {
                res.send("Hello World!");
              });
           })
          .build();
        server.start();
        """, null).build();
      context.eval(source);
    });
  }

}

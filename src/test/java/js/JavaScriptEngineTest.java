package js;

import js.test.Test1;
import org.graalvm.polyglot.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JavaScriptEngineTest {

  private final Logger log = LoggerFactory.getLogger(JavaScriptEngineTest.class);

  @Test
  public void test() {
    try (Context context = Context.newBuilder("js")
      .engine(Engine.newBuilder().build())
      .allowExperimentalOptions(true)
      .allowHostAccess(HostAccess.ALL)
      .allowHostClassLookup(clazz -> {
        log.debug("ClassLookup:{}", clazz);
        return true;
      })
      // 打印未处理的异常
      .allowExperimentalOptions(true).option("js.unhandled-rejections", "warn")
      .build()) {
      Test1 test = new Test1();
      context.getBindings("js").putMember("testObj", test);
      Source source = Source.newBuilder("js", """
        console.log(`testObj: ${testObj}`);
        console.log(`Java.isJavaObject:${Java.isJavaObject(testObj)}`)
        console.log(`name: ${testObj.name}`);
        // testObj.test();
        """, "test1#test").build();
      context.eval(source);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}

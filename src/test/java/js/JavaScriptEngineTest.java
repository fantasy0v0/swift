package js;

import js.test.Test1;
import org.graalvm.polyglot.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;

public class JavaScriptEngineTest {

  private static final Logger log = LoggerFactory.getLogger(JavaScriptEngineTest.class);

  public static void createContext(ConsumerWithException<Context> consumer) {
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
      consumer.accept(context);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void test() {
    createContext(context -> {
      Test1 test = new Test1();
      context.getBindings("js")
        .putMember("testObj", test);
      Source source = Source.newBuilder("js", """
        console.log(`testObj: ${testObj}`);
        console.log(`Java.isJavaObject:${Java.isJavaObject(testObj)}`)
        testObj.test();
        testObj.test1(() => {
          console.log('Test1#test1');
        });
        testObj.test2(name => {
          console.log(`Test1#test2, Hello ${name}`);
        });
        """, "test1#test").build();
      context.eval(source);
    });
  }

}

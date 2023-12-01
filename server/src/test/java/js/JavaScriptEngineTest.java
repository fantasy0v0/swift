package js;

import js.test.Test1;
import org.graalvm.polyglot.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.ReentrantLock;

public class JavaScriptEngineTest {

  private static final Logger log = LoggerFactory.getLogger(JavaScriptEngineTest.class);

  public static void createContext(ConsumerWithException<Context> consumer) throws Exception {
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
    }
  }

  @Test
  public void test() throws Exception {
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

  @Test
  public void multiThread() throws InterruptedException {
    long start = System.currentTimeMillis();
    CountDownLatch latch = new CountDownLatch(10000);
    ThreadFactory factory = Thread.ofVirtual().name("java-", 1).factory();
    for (int i = 0; i < 10000; i++) {
      factory.newThread(() -> {
        String name = Thread.currentThread().getName();
        System.out.println(name);
        latch.countDown();
      }).start();
    }
    latch.await();
    System.out.println(System.currentTimeMillis() - start);
  }

  @Test
  public void multiThreadByJs() throws Exception {
    Source source = Source.newBuilder("js", """
      let a = {
        test: function (thread) {
          console.log(thread.getName());
        }
      }
      a;
    """, null).build();
    long start = System.currentTimeMillis();
    CountDownLatch latch = new CountDownLatch(10000);
    ReentrantLock contextLock = new ReentrantLock();
    ThreadFactory factory = Thread.ofVirtual().name("js-", 1).factory();
    createContext(context -> {
      Value testFunction = context.eval(source).getMember("test");
      for (int i = 0; i < 10000; i++) {
        factory.newThread(() -> {
          contextLock.lock();
          Thread thread = Thread.currentThread();
          testFunction.executeVoid(thread);
          latch.countDown();
          contextLock.unlock();
        }).start();
      }
      latch.await();
      System.out.println(System.currentTimeMillis() - start);
    });
  }

  @Test
  public void multiThreadByJsWithSharedEngine() throws Exception {
    Source source = Source.newBuilder("js", """
      let a = {
        test: function (thread) {
          console.log(thread.getName());
        }
      }
      a;
    """, null).build();

    Engine sharedEngine = Engine.newBuilder().build();
    List<Value> pool = new ArrayList<>(10000);
    for (int i = 0; i < 10000; i++) {
      Context context = Context.newBuilder("js")
        .engine(sharedEngine).allowExperimentalOptions(true)
        .allowHostAccess(HostAccess.ALL).build();
      Value testFunction = context.eval(source).getMember("test");
      pool.add(testFunction);
    }
    long start = System.currentTimeMillis();
    CountDownLatch latch = new CountDownLatch(10000);
    ThreadFactory factory = Thread.ofVirtual().name("js-", 1).factory();
    for (int i = 0; i < 10000; i++) {
      Value testFunction = pool.get(i);
      factory.newThread(() -> {
        try {
          Thread thread = Thread.currentThread();
          testFunction.executeVoid(thread);
        } finally {
          latch.countDown();
        }
      }).start();
    }
    latch.await();
    System.out.println(System.currentTimeMillis() - start);
  }

}

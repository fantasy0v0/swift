package js.test;

import org.graalvm.polyglot.HostAccess;

public class Test1 {

  public void test() {
    System.out.println("Test1#test");
  }

  public void test1(Runnable runnable) {
    if (null != runnable) {
      runnable.run();
    }
  }

}

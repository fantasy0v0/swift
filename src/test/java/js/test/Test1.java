package js.test;

import org.graalvm.polyglot.HostAccess;

public class Test1 {

  @HostAccess.Export
  public String name = "test1";

  public void test() {
    System.out.println("Test1#test");
  }

}

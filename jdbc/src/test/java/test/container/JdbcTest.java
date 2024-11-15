package test.container;

public record JdbcTest(String name,
                       JdbcExecutable executable) {

  public static JdbcTest of(String name, JdbcExecutable executable) {
    return new JdbcTest(name, executable);
  }

}

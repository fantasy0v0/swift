package test.container;

/**
 * @author fan 2025/8/28
 */
public enum Db {

  Postgres("postgres"),

  MySQL("mysql");

  final String name;

  Db(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}

<p align="center">
  <img width="256" height="256" src="jdbc/logo.webp">
</p>

# swift-jdbc

[![License](https://img.shields.io/badge/license-GNU-blue.svg)](LICENSE)
[![JDK 21](https://img.shields.io/badge/JDK-21-green.svg)](https://openjdk.org/projects/jdk/21/)

专为JDBC操作设计的轻量级工具库，提供以下核心能力：

✨ **简洁高效**

▸ 零反射操作

▸ 原生JDBC性能，无复杂抽象层开销

▸ 流畅API设计，链式调用自然直观

🛠️ **功能完备**

▸ 多级事务管理（支持嵌套事务）

▸ 批量操作优化

🔌 **兼容并蓄**

与主流ORM框架（JPA/MyBatis/Jooq等）无缝协作，专注填补以下场景：

✅ 快速执行SQL

✅ SQL in Code

✅ 遗留系统改造过渡

📚 **学习零成本**

API设计遵循JDBC原生语义，开发者无需学习新概念即可快速上手

## 如何使用

**maven**
```xml
<project>
  <repositories>
    <repository>
      <id>github</id>
      <name>GitHub fantasy0v0 Apache Maven Packages</name>
      <url>https://maven.pkg.github.com/fantasy0v0/repository</url>
    </repository>
  </repositories>

  <dependencies>
    <dependency>
      <groupId>com.github.fantasy0v0.swift</groupId>
      <artifactId>swift-jdbc</artifactId>
      <version>1.3.1</version>
    </dependency>
  </dependencies>
</project>
```

## 注意事项

如果想在spring环境下(比如@Transaction)使用spring的事务能力, 需要添加jdbc-spring-support依赖。

如果没有该依赖, swift-jdbc将会获取一个新的数据库连接来开启事务, 这两个连接同时使用的话, 容易产生死锁问题

```xml
<dependency>
  <groupId>com.github.fantasy0v0.swift</groupId>
  <artifactId>swift-jdbc-spring-support</artifactId>
  <version>1.3.1</version>
</dependency>
```

# 样例

## 配置

在使用前进行如下配置即可

```java
DataSource dataSource = DataSourceUtil.create();
JDBC.initialization(dataSource);
```

## select

### 查询所有记录(无映射参数)

如果不传递映射参数, 则默认返回的泛型类型为Object[]

```java
List<Object[]> students = select("""
select * from student
""").fetch();
```

### 查询所有记录(有映射参数, 预编译参数设置)

```java
select("""
select id, name, status from student where id = ?
""", 1L).fetchOne(
  row -> new Student(row.getLong(1), row.getString(2), row.getLong(3))
);
```

### 动态sql条件

临时方案

```java
String sql = "select * from student";
List<Object> parameters = new ArrayList<>();
Predicate predicate = and(
  exp("id > ?", 0),
  exp("status = ?", 2)
);
sql = where(sql, predicate);
parameters.addAll(predicate.getParameters());
sql += " order by id asc";
sql += " fetch first 20 row only";
List<Student> students = select(sql, parameters)
  .fetch(Student::from);
```

### 分页

```java
PagingData<Student> data = select("""
select * from student
""").paginate(0, 10).fetch(Student::from);
```

### 支持获取PostgreSQL数组

```java
List<List<String>> arrays = select("""
  select tags from swift_user where tags is not null
  """).fetch(row -> row.getArray(1, String.class));
```

## 修改操作

insert
```java
int executed = JDBC.insert("""
insert into student(id, name, status)
values(1000, '测试学生', 0)
""").execute();
```

支持postgres的returning
```java
Long result = JDBC.insert("""
insert into student(id, name, status)
values(?, ?, ?)
returning id
""").fetchOne(row -> row.getLong(1), 1000L, "测试学生", 0);
```

获取生成的主键
```java
long key = JDBC.insert("""
insert into swift_user(name, status) values('测试学生', 0)
""").fetchKey(row -> row.getLong(1));
```

批量插入
```java
List<List<Object>> batchParams = new ArrayList<>();
batchParams.add(List.of(1000, "测试用户1", 0));
batchParams.add(List.of(1001, "测试用户2", 1));
batchParams.add(List.of(1002, "测试用户3", 2));
batchParams.add(List.of(1003, "测试用户4", 3));
batchParams.add(List.of(1004, "测试用户5", 4));
batchParams.add(List.of(1005, "测试用户6", 5));

int[] executed = JDBC.modify("""
insert into student(id, name, status)
values(?, ?, ?)
""").batch(batchParams);
```

update
```java
int executed = JDBC.update("""
update student set name = ? where id = ?
""").execute("测试修改", 1);
```

## 事务

### 开启事务
```java
transaction(() -> {
  modify("update student set name = ? where id = ?")
    .execute("修改", 1L);
});
```

也可以明确指定事务级别

```java
transaction(Connection.TRANSACTION_READ_COMMITTED, () -> {
  modify("update student set name = ? where id = ?")
    .execute("修改", 1L);
});
```

当参数中Lambda方法正常执行完成时, transaction方法会将创建的事务提交, 如果抛出了异常, 则会进行回滚, 并将异常继续向上抛出, 由使用者根据自己的业务自行处理

### 嵌套事务

该方法期望能灵活地处理嵌套事务的问题, 内部物抛出异常, 将会被回滚, 但如果外部事务对该异常进行捕获, 将不会导致外部事务回滚。

> [!CAUTION]
> 不支持在事务中修改隔离级别, 仅在最开始的事务中设置, 后续嵌套的事务隔离级别将不会生效(部分JDBC会直接报错)

```java
transaction(Connection.TRANSACTION_READ_UNCOMMITTED, () -> {
  select("select * from student").fetch();
  transaction(() -> {
    select("select * from student").fetch();
    transaction(() -> {
      modify("update student set name = ? where id = ?")
        .execute("修改", 1L);
    });
  });
});
```

### 支持带返回值的Lambda
```java
public Long getId() {
  return transaction(() -> {
    return select("""
    select id from student limit 1
    """).fetchOne(row -> row.getLong(1));
  });
}
```

## 调试功能

## 查看SQL执行时间

将"com.github.fantasy0v0.swift.jdbc.performance"的日志级别设置为TRACE、DEBUG时, 会在日志中打印执行时间

```text
10:51:36:846 TRACE executeQuery begin
10:51:36:846 DEBUG executeQuery cost: 2,413 μs
```

## 打印执行的SQL

将"com.github.fantasy0v0.swift.jdbc.sql"的日志级别设置为DEBUG时, 会在日志中打印执行的SQL, 日志内容还包含调用者的信息(caller), 方便开发人员快速定位到调用位置

```text
10:51:36:884 DEBUG executeUpdate: update student set name = ? where id = ?, caller: test.UpdateTest(UpdateTest.java:39)
```

## 打印参数信息

将"com.github.fantasy0v0.swift.jdbc.sql"的日志级别设置为TRACE、DEBUG时, 会在日志中打印参数信息,
日志内容包含参数的数量, 下标, 内容, 以及使用了哪个parameter handler

```text
10:51:36:884 DEBUG parameter count: 2
10:51:36:884 TRACE fill parameter: [1] - [测试修改], use global parameter handler
10:51:36:884 TRACE fill parameter: [2] - [1], use global parameter handler
```

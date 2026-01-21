<p align="center">
  <img width="256" height="256" src="core/logo.webp">
</p>

# swift

[![License](https://img.shields.io/badge/license-GNU-blue.svg)](LICENSE)
[![JDK 21](https://img.shields.io/badge/JDK-21-green.svg)](https://openjdk.org/projects/jdk/21/)

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
      <artifactId>swift-core</artifactId>
      <version>0.0.1-SNAPSHOT</version>
    </dependency>
  </dependencies>
</project>
```

## 注意事项

本工具使用ThreadLocal管理连接，非线程安全，不能跨线程使用。

如果想在spring环境下(比如@Transaction)使用spring的事务能力, 需要添加jdbc-spring-support依赖。

如果没有该依赖, swift-jdbc将会获取一个新的数据库连接来开启事务, 这两个连接同时使用的话, 容易产生死锁问题

```xml
<dependency>
  <groupId>com.github.fantasy0v0.swift</groupId>
  <artifactId>swift-spring</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

# 样例

## 配置

在使用前进行如下配置即可

```java
import com.github.fantasy0v0.swift.Swift;

DataSource dataSource = DataSourceUtil.create();
Swift.setContext(Swift.newContext(dataSource));
```

## select

### 查询所有记录(无映射参数)

如果不传递映射参数, 则默认返回的类型为Object[]

```java
List<Object[]> students = select("""
select * from student
""").fetch();
// 返回Map
List<Map<String, Object>> students1 = select("""
select id, name, status from student where id = ?
""", 1L).fetch(Row::toMap);
```

### 查询单条记录(有映射参数, 预编译参数设置)

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

### insert
```java
int executed = Swift.insert("""
insert into student(id, name, status)
values(1000, '测试学生', 0)
""").execute();
```

### 支持postgres的returning
```java
Long result = Swift.insert("""
insert into student(id, name, status)
values(?, ?, ?)
returning id
""").fetchOne(row -> row.getLong(1), 1000L, "测试学生", 0);
```

### 获取生成的主键
```java
long key = Swift.insert("""
insert into swift_user(name, status) values('测试学生', 0)
""").fetchKey(row -> row.getLong(1));
```

#### PostgreSQL注意事项

建议PostgreSQL使用fetchKey时，额外加一段returning id，否则PostgreSQL会把整行数据返回

相关讨论：https://stackoverflow.com/questions/19766816/postgresql-jdbc-getgeneratedkeys-returns-all-columns

```java
long key = Swift.insert("""
insert into swift_user(name, status) values('测试学生1', 0) returning id
""").fetchKey(row -> row.getLong(1));
```

### 批量插入
```java
List<List<Object>> batchParams = new ArrayList<>();
batchParams.add(List.of(1000, "测试用户1", 0));
batchParams.add(List.of(1001, "测试用户2", 1));
batchParams.add(List.of(1002, "测试用户3", 2));
batchParams.add(List.of(1003, "测试用户4", 3));
batchParams.add(List.of(1004, "测试用户5", 4));
batchParams.add(List.of(1005, "测试用户6", 5));

int[] executed = Swift.modify("""
insert into student(id, name, status)
values(?, ?, ?)
""").batch(batchParams);
```

### update
```java
int executed = Swift.update("""
update student set name = ? where id = ?
""").execute("测试修改", 1);
```

## 事务

### 开启事务
```java
transaction(() -> {

update("update student set name = ? where id = ?")
    .execute("修改", 1L);
});
```

也可以明确指定事务级别

```java
transaction(Connection.TRANSACTION_READ_COMMITTED, () -> {

update("update student set name = ? where id = ?")
    .execute("修改", 1L);
});
```

当参数中Lambda方法正常执行完成时, transaction方法会将创建的事务提交, 如果抛出了异常, 则会进行回滚, 并将异常继续向上抛出, 由使用者根据自己的业务自行处理

#### 事务共享

多个transaction会共享事务，类似Spring的Propagation.REQUIRED，如果想要做到部分回滚，请浏览[保存点](#保存点-Savepoint)章节

> [!CAUTION]
> 不支持在事务中修改隔离级别, 仅在最外层或Spring的事务中设置, 后续设置事务隔离级别将不会生效(部分JDBC会直接报错)

```java
transaction(Connection.TRANSACTION_READ_UNCOMMITTED, () -> {
  select("select * from student").fetch();
  transaction(() -> {
    select("select * from student").fetch();
    transaction(() -> {

update("update student set name = ? where id = ?")
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

### 保存点 Savepoint

该功能与数据库中的保存点功能一致，可以在transaction中做到部分回滚

```java
transaction(Connection.TRANSACTION_READ_UNCOMMITTED, () ->{

select("select * from student").

fetch();

savepoint(() ->{

update("update student set name = ? where id = ?")
      .

execute("修改",1L);
  });
  });
```

## 调试功能

## 查看SQL执行时间

将"com.github.fantasy0v0.swift.performance"的日志级别设置为TRACE、DEBUG时, 会在日志中打印执行时间

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

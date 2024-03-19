<p align="center">
  <img width="256" height="256" src="jdbc/logo.webp">
</p>

# swift-jdbc

配置简单, 使用方便, 在不额外学习其他框架的情况下快速方便的使用jdbc进行CRUD。

欢迎大家提提意见和贡献代码。

## 如何使用

**maven**
```xml
<project>
  <repositories>
    <repository>
      <id>github</id>
      <name>GitHub fantasy0v0 Apache Maven Packages</name>
      <url>https://maven.pkg.github.com/fantasy0v0/swift</url>
    </repository>
  </repositories>

  <dependencies>
    <dependency>
      <groupId>com.github.fantasy0v0.swift</groupId>
      <artifactId>jdbc-spring-support</artifactId>
      <version>1.0-SNAPSHOT</version>
    </dependency>
  </dependencies>
</project>
```

## 环境要求

由于使用了一些特性, 所以Java版本限制在21或以上

# 样例

## 配置

在使用前进行如下配置即可

```java
DataSource dataSource = DataSourceUtil.create();
JDBC.configuration(dataSource);
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
""",1L).fetchOne(row -> new Student(
  row.getLong(1),
  row.getString(2),
  row.getLong(3)
));
```

### 动态sql条件

TODO 可先查看SelectTest#testPredicate单元测试

### 分页

TODO 可先查看PagingTest单元测试

## modify

TODO 可先查看InsertTest、UpdateTest单元测试

## 事务

TODO 可先查看TransactionTest单元测试

# 适合用于在junit环境下需要回滚的场景

> 如果你处于spring环境可以去看swift-spring

## 使用方法

在需要回滚的单元测试类中添加如下注解

```java
@ExtendWith(SwiftJdbcExtension.class)
```

## 初始化

```java

import com.github.fantasy0v0.swift.Swift;

@BeforeAll
static void beforeAll() throws SQLException {
  dataSource = DataSourceUtil.create();
  Swift.setContext(Swift.newContext(dataSource));
}

@AfterAll
static void afterAll() {
  if (null != dataSource) {
    dataSource.close();
  }
}
```

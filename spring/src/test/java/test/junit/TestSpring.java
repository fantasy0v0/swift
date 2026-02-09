package test.junit;

import com.github.fantasy0v0.swift.Swift;
import com.github.fantasy0v0.swift.exception.SwiftException;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import test.service.TestService;

@Transactional
@SpringBootTest
public class TestSpring {

  @Resource
  private TestService testService;

  @Autowired
  private PlatformTransactionManager transactionManager;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Test
  void test() {
    try {
      Swift.savepoint(() -> {
        Swift.update("""
        update student set name = ? where id = ?
        """).execute("大明", 1);
        throw new SwiftException("测试");
      });
    } catch (SwiftException e) {
      Assertions.assertEquals("测试", e.getMessage());
    }
    String name = Swift.select("""
    select name from student where id = ?
    """, 1).fetchOne(row -> row.getString(1));
    Assertions.assertEquals("小明", name);

    Swift.transaction(() -> {
      Swift.update("""
      update student set name = ? where id = ?
      """).execute("大明", 1);
    });
    name = Swift.select("""
    select name from student where id = ?
    """, 1).fetchOne(row -> row.getString(1));
    Assertions.assertEquals("大明", name);
  }

  /**
   * 如果不设置propagation = Propagation.REQUIRES_NEW, 会让代码走进AbstractPlatformTransactionManager:900-906的分支中, 事务只会被标记为仅回滚, 不会立即进行回滚操作
   * <p>先记录一下</p>
   * <p>如果从头到尾都是Propagation.REQUIRES的话, 内部方法抛出的异常不会立即回滚, 事务只会被标记为仅回滚, 只有当最开始开启事务的方法结束时, 才会进行回滚</p>
   * <p>这种嵌套多个事务(仅REQUIRES)被称为larger transaction</p>
   * <p>如果想在一个大事务中回滚部分事务, 需要设置为REQUIRES_NEW</p>
   */
  @Test
  void test2() {
    try {
      testService.test(true);
      Assertions.fail();
    } catch (SwiftException e) {
      // ignore
    }
//    String name = jdbcTemplate.queryForObject(
//    "select name from student where id = ?", String.class, 1);
//    Assertions.assertEquals("小明", name);
//    name = JDBC.select("""
//    select name from student where id = ?
//    """, 1).fetchOne(row -> row.getString(1));
//    Assertions.assertEquals("小明", name);
//
//    testService.test(false);
//    name = JDBC.select("""
//    select name from student where id = ?
//    """, 1).fetchOne(row -> row.getString(1));
//    Assertions.assertEquals("大明", name);
    int result = jdbcTemplate.update(
      "update student set name = ? where id = ?", "大明2", 1);
    Assertions.assertEquals(1, result);
  }

  @Test
  void test3() {
    try {
      testService.testRequiresNew(true);
      Assertions.fail();
    } catch (SwiftException e) {
      // ignore
    }
    String name = jdbcTemplate.queryForObject(
      "select name from student where id = ?", String.class, 1);
    Assertions.assertEquals("小明", name);
    name = Swift.select("""
    select name from student where id = ?
    """, 1).fetchOne(row -> row.getString(1));
    Assertions.assertEquals("小明", name);

    // 这里修改数据会影响其他测试用例, 先注释掉。
    // testService.testRequiresNew(false);
    // name = Swift.select("""
    // select name from student where id = ?
    // """, 1).fetchOne(row -> row.getString(1));
    // Assertions.assertEquals("大明", name);
  }

}

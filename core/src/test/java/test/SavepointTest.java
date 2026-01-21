package test;

import com.github.fantasy0v0.swift.exception.SwiftException;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.SwiftJdbcExtension;
import test.container.Transactional;

import java.util.function.Supplier;

import static com.github.fantasy0v0.swift.Swift.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author fan 2026/1/21
 */
@ExtendWith(SwiftJdbcExtension.class)
class SavepointTest {

  private final Logger log = LoggerFactory.getLogger(SavepointTest.class);

  @TestTemplate
  @Transactional
  void test() {
    Supplier<String> getName = () -> select("select name from student where id = ?", 1L).fetchOne(row -> row.getString(1));
    transaction(() -> {
      var name = getName.get();
      assertEquals("小明", name);
      savepoint(() -> {
        var updated = update("update student set name = ? where id = ?").execute("fantasy", 1L);
        assertEquals(1, updated);
        var name1 = getName.get();
        assertEquals("fantasy", name1);
      });
      name = getName.get();
      assertEquals("fantasy", name);
    });

    transaction(() -> {
      var name = getName.get();
      assertEquals("fantasy", name);
      try {
        savepoint(() -> {
          var updated = update("update student set name = ? where id = ?").execute("fantasy1", 1L);
          assertEquals(1, updated);
          var name1 = getName.get();
          assertEquals("fantasy1", name1);
          throw new RuntimeException("rollback");
        });
        fail();
      } catch (RuntimeException e) {
        assertEquals("rollback", e.getMessage());
      }
      name = getName.get();
      assertEquals("fantasy", name);
    });
  }

  @TestTemplate
  void testReturn() {
    var name = transaction(() -> {
      return savepoint(() -> {
        return select("select name from student where id = ?", 1L).fetchOne(row -> row.getString(1));
      });
    });
    assertEquals("小明", name);
  }

  @TestTemplate
  void testWithoutTransaction() {
    SwiftException exception = assertThrowsExactly(SwiftException.class, () -> {
      var list = savepoint(() -> select("select * from student").fetch());
      log.info("{}", list);
    });
    assertEquals("savepoint must be used in a transaction", exception.getMessage());
  }

  @TestTemplate
  @Transactional
  void testNestedSavepoint() {
    Supplier<String> getName = () -> select("select name from student where id = ?", 1L).fetchOne(row -> row.getString(1));
    var name1 = getName.get();
    assertEquals("小明", name1);
    transaction(() -> {
      savepoint(() -> {
        update("update student set name = ? where id = ?").execute("level1", 1L);
        var name2 = getName.get();
        assertEquals("level1", name2);
        try {
          savepoint(() -> {
            update("update student set name = ? where id = ?").execute("level2", 1L);
            var name3 = getName.get();
            assertEquals("level2", name3);
            throw new RuntimeException("rollback level2");
          });
          fail();
        } catch (RuntimeException e) {
          assertEquals("rollback level2", e.getMessage());
        }
        // level2 回滚了，但 level1 的修改应该保留
        var name = getName.get();
        assertEquals("level1", name);
      });
    });
  }

}

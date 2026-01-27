package test.junit;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.fantasy0v0.swift.Swift.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TransactionTest {

  private final Logger log = LoggerFactory.getLogger(TransactionTest.class);

  @Test
  void rollback() {
    long testId = 1L;
    String oldName = select("select name from student where id = ?", testId).fetchOne(row -> row.getString(1));
    log.debug("name: {}", oldName);
    try {
      transaction(() -> {
        update("update student set name = ? where id = ?").execute("修改", 1L);
        String newName = select("select name from student where id = ?", testId).fetchOne(row -> row.getString(1));
        assertEquals("修改", newName);
        throw new RuntimeException("rollback");
      });
    } catch (RuntimeException e) {
      assertEquals("rollback", e.getMessage());
    }
    String name = select("select name from student where id = ?", testId).fetchOne(row -> row.getString(1));
    assertEquals(oldName, name);

    try {
      transaction(() -> {
        transaction(() -> {
          update("update student set name = ? where id = ?").execute("修改", 1L);
        });
        String newName = select("select name from student where id = ?", testId).fetchOne(row -> row.getString(1));
        assertEquals("修改", newName);
        throw new RuntimeException("rollback");
      });
    } catch (RuntimeException e) {
      assertEquals("rollback", e.getMessage());
    }
    name = select("select name from student where id = ?", testId).fetchOne(row -> row.getString(1));
    assertEquals(oldName, name);
  }

}

package test;

import com.github.fantasy0v0.swift.jdbc.JDBC;
import com.github.fantasy0v0.swift.jdbc.PagingData;
import com.github.fantasy0v0.swift.jdbc.predicate.Predicate;
import com.zaxxer.hikari.HikariDataSource;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.vo.Student;

import java.sql.SQLException;

import static com.github.fantasy0v0.swift.jdbc.JDBC.select;
import static com.github.fantasy0v0.swift.jdbc.clauses.Clauses.where;
import static com.github.fantasy0v0.swift.jdbc.predicate.Predicates.exp;

class PagingTest {

  private final Logger log = LoggerFactory.getLogger(PagingTest.class);

  private static HikariDataSource dataSource;

  @BeforeAll
  static void beforeAll() throws SQLException {
    dataSource = DataSourceUtil.create();
    JDBC.configuration(dataSource);
  }

  @AfterAll
  static void afterAll() {
    if (null != dataSource) {
      dataSource.close();
    }
  }

  @Test
  void testWithOutWhere() throws SQLException {
    PagingData<Student> data = select("select * from student")
      .paging(0, 10)
      .fetch(Student::from);
    log.debug("total: {}", data.total());
    log.debug("totalPages: {}", data.totalPages());
    log.debug("dataSize: {}", data.data().size());
    Assertions.assertEquals(5, data.total());
    Assertions.assertEquals(1, data.totalPages());
    Assertions.assertEquals(5, data.data().size());

    data = select("select * from student")
      .paging(0, 2)
      .fetch(Student::from);
    log.debug("total: {}", data.total());
    log.debug("totalPages: {}", data.totalPages());
    log.debug("dataSize: {}", data.data().size());
    Assertions.assertEquals(5, data.total());
    Assertions.assertEquals(3, data.totalPages());
    Assertions.assertEquals(2, data.data().size());

    data = select("select * from student")
      .paging(0, 5)
      .fetch(Student::from);
    log.debug("total: {}", data.total());
    log.debug("totalPages: {}", data.totalPages());
    log.debug("dataSize: {}", data.data().size());
    Assertions.assertEquals(5, data.total());
    Assertions.assertEquals(1, data.totalPages());
    Assertions.assertEquals(5, data.data().size());
  }

  @Test
  void testWithWhere() {
    @Language("SQL") String sql = "select * from student";
    Predicate predicate = exp("status = ?", 2);
    sql = where(sql, predicate);
    PagingData<Student> data = select(sql, predicate.getParameters())
      .paging(0, 10)
      .fetch(Student::from);
    log.debug("total: {}", data.total());
    log.debug("totalPages: {}", data.totalPages());
    log.debug("dataSize: {}", data.data().size());
    Assertions.assertEquals(3, data.total());
    Assertions.assertEquals(1, data.totalPages());
    Assertions.assertEquals(3, data.data().size());
  }

  @Test
  void customizeCount() {
    PagingData<Student> data = select("select * from student")
      .paging(0, 10)
      .count("select count(1) from student where status = ?", 2)
      .fetch(Student::from);
    log.debug("total: {}", data.total());
    log.debug("totalPages: {}", data.totalPages());
    log.debug("dataSize: {}", data.data().size());
    Assertions.assertEquals(3, data.total());
    Assertions.assertEquals(1, data.totalPages());
    Assertions.assertEquals(5, data.data().size());
  }

}

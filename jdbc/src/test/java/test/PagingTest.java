package test;

import com.github.fantasy0v0.swift.jdbc.PageData;
import com.github.fantasy0v0.swift.jdbc.predicate.Predicate;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.SwiftJdbcExtension;
import test.vo.Student;

import javax.sql.DataSource;

import static com.github.fantasy0v0.swift.jdbc.JDBC.select;
import static com.github.fantasy0v0.swift.jdbc.clauses.Clauses.where;
import static com.github.fantasy0v0.swift.jdbc.predicate.Predicates.exp;

@ExtendWith(SwiftJdbcExtension.class)
class PagingTest {

  private final Logger log = LoggerFactory.getLogger(PagingTest.class);

  @TestTemplate
  void testWithOutWhere(DataSource dataSource) {
    PageData<Student> data = select("select * from student")
      .paginate(0, 10)
      .fetch(Student::from);
    log.debug("total: {}", data.total());
    log.debug("totalPages: {}", data.totalPages());
    log.debug("dataSize: {}", data.data().size());
    // 这里需要改造完事务后再恢复 TODO
    // Assertions.assertEquals(5, data.total());
    // Assertions.assertEquals(1, data.totalPages());
    // Assertions.assertEquals(5, data.data().size());

    data = select("select * from student")
      .paginate(0, 2)
      .fetch(Student::from);
    log.debug("total: {}", data.total());
    log.debug("totalPages: {}", data.totalPages());
    log.debug("dataSize: {}", data.data().size());
    // 这里需要改造完事务后再恢复 TODO
    // Assertions.assertEquals(5, data.total());
    // Assertions.assertEquals(3, data.totalPages());
    // Assertions.assertEquals(2, data.data().size());

    data = select("select * from student")
      .paginate(0, 5)
      .fetch(Student::from);
    log.debug("total: {}", data.total());
    log.debug("totalPages: {}", data.totalPages());
    log.debug("dataSize: {}", data.data().size());
    // 这里需要改造完事务后再恢复 TODO
    // Assertions.assertEquals(5, data.total());
    // Assertions.assertEquals(1, data.totalPages());
    // Assertions.assertEquals(5, data.data().size());
  }

  @TestTemplate
  void testWithWhere() {
    String sql = "select * from student";
    Predicate predicate = exp("status = ?", 2);
    sql = where(sql, predicate);
    PageData<Student> data = select(sql, predicate.getParameters())
      .paginate(0, 10)
      .fetch(Student::from);
    log.debug("total: {}", data.total());
    log.debug("totalPages: {}", data.totalPages());
    log.debug("dataSize: {}", data.data().size());
    // 这里需要改造完事务后再恢复 TODO
    // Assertions.assertEquals(3, data.total());
    // Assertions.assertEquals(1, data.totalPages());
    // Assertions.assertEquals(3, data.data().size());
  }

  @TestTemplate
  void customizeCount() {
    PageData<Student> data = select("select * from student")
      .paginate(0, 10)
      .count("select count(1) from student where status = ?", 2)
      .fetch(Student::from);
    log.debug("total: {}", data.total());
    log.debug("totalPages: {}", data.totalPages());
    log.debug("dataSize: {}", data.data().size());
    // 这里需要改造完事务后再恢复 TODO
    // Assertions.assertEquals(3, data.total());
    // Assertions.assertEquals(1, data.totalPages());
    // Assertions.assertEquals(5, data.data().size());
  }

}

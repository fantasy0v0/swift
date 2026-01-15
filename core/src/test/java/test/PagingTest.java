package test;

import com.github.fantasy0v0.swift.PageData;
import com.github.fantasy0v0.swift.predicate.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.SwiftJdbcExtension;
import test.vo.Student;

import static com.github.fantasy0v0.swift.Swift.select;
import static com.github.fantasy0v0.swift.clauses.Clauses.where;
import static com.github.fantasy0v0.swift.predicate.Predicates.exp;

@ExtendWith(SwiftJdbcExtension.class)
class PagingTest {

  private final Logger log = LoggerFactory.getLogger(PagingTest.class);

  @TestTemplate
  void testWithOutWhere() {
    PageData<Student> data = select("select * from student")
      .paginate(0, 10)
      .fetch(Student::from);
    log.debug("total: {}", data.total());
    log.debug("totalPages: {}", data.totalPages());
    log.debug("dataSize: {}", data.data().size());
    Assertions.assertEquals(5, data.total());
    Assertions.assertEquals(1, data.totalPages());
    Assertions.assertEquals(5, data.data().size());

    data = select("select * from student")
      .paginate(0, 2)
      .fetch(Student::from);
    log.debug("total: {}", data.total());
    log.debug("totalPages: {}", data.totalPages());
    log.debug("dataSize: {}", data.data().size());
    Assertions.assertEquals(5, data.total());
    Assertions.assertEquals(3, data.totalPages());
    Assertions.assertEquals(2, data.data().size());

    data = select("select * from student")
      .paginate(0, 5)
      .fetch(Student::from);
    log.debug("total: {}", data.total());
    log.debug("totalPages: {}", data.totalPages());
    log.debug("dataSize: {}", data.data().size());
    Assertions.assertEquals(5, data.total());
    Assertions.assertEquals(1, data.totalPages());
    Assertions.assertEquals(5, data.data().size());
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
    Assertions.assertEquals(3, data.total());
    Assertions.assertEquals(1, data.totalPages());
    Assertions.assertEquals(3, data.data().size());
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
    Assertions.assertEquals(3, data.total());
    Assertions.assertEquals(1, data.totalPages());
    Assertions.assertEquals(5, data.data().size());
  }

}

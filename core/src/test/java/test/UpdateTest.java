package test;

import com.github.fantasy0v0.swift.Swift;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.Allowed;
import test.container.Db;
import test.container.SwiftJdbcExtension;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ExtendWith(SwiftJdbcExtension.class)
public class UpdateTest {

  private final Logger log = LoggerFactory.getLogger(UpdateTest.class);

  @TestTemplate
  void testExecuteUpdate(DataSource dataSource) {
    int executed = Swift.update("""
      update student set name = ? where id = ?
      """).execute("测试修改", 1);
    Assertions.assertEquals(1, executed);
    executed = Swift.update("""
      update student set name = ? where id = ?
      """).execute("测试修改1", 1);
    Assertions.assertEquals(1, executed);
    executed = Swift.update("""
      update student set name = '测试修改3'
      """).execute();
    Assertions.assertTrue(executed > 1);
  }

  @TestTemplate
  @Allowed(Db.Postgres)
  void testFetch() {
    List<Object[]> result = Swift.update("""
      update student set name = ? where id = ? returning id
      """).fetch("测试修改0", 1);
    Assertions.assertEquals(1, result.size());
    Assertions.assertEquals(1L, result.getFirst()[0]);

    result = Swift.update("""
      update student set name = ? returning id
      """).fetch("测试修改1");
    Assertions.assertTrue(result.size() > 1);

    Object[] fetchOne2 = Swift.update("""
      update student set name = ? where id = ? returning id, name
      """).fetchOne("测试修改2", 1L);
    Assertions.assertEquals(2, fetchOne2.length);
    Assertions.assertEquals(1L, fetchOne2[0]);
    Assertions.assertEquals("测试修改2", fetchOne2[1]);

    long fetchOne3 = Swift.update("""
      update student set name = ? where id = ? returning id
      """).fetchOne(row -> row.getLong(1), "测试修改3", 1L);
    Assertions.assertEquals(1L, fetchOne3);
    String actualName = Swift.select("select name from student where id = ?", 1L)
      .fetchOne(row -> row.getString(1));
    Assertions.assertEquals("测试修改3", actualName);

    Object[] fetchOne4 = Swift.update("""
      update student set name = '测试修改4' where id = ? returning id, name
      """).fetchOne(1L);
    Assertions.assertEquals(2, fetchOne4.length);
    Assertions.assertEquals(1L, fetchOne4[0]);
    Assertions.assertEquals("测试修改4", fetchOne4[1]);

    List<Object> fetchOne5Params = new ArrayList<>();
    fetchOne5Params.add("测试修改5");
    fetchOne5Params.add(1L);
    Object[] fetchOne5 = Swift.update("""
      update student set name = ? where id = ? returning id, name
      """).fetchOne(fetchOne5Params);
    Assertions.assertEquals(2, fetchOne5.length);
    Assertions.assertEquals(1L, fetchOne5[0]);
    Assertions.assertEquals("测试修改5", fetchOne5[1]);

    long fetchOne6 = Swift.update("""
      update student set name = '测试修改6' where id = 1 returning id
      """).fetchOne(row -> row.getLong(1));
    Assertions.assertEquals(1L, fetchOne6);
  }

  @TestTemplate
  void testArrayParams(DataSource dataSource) {
    Object[] params = {"测试修改", 1L};
    int executed = Swift.update("""
      update student set name = ? where id = ?
      """).execute(params);
    Assertions.assertEquals(1, executed);
    Object[] result = Swift.select("""
      select name, id from student where id = ?
      """, params[1]).fetchOne();
    Assertions.assertEquals(params[0], result[0]);
    Assertions.assertEquals(params[1], result[1]);
  }

  @TestTemplate
  void testBatch(DataSource dataSource) {
    int[] executedBatch = Swift.update("""
      update student set name = ? where id = ?
      """).batch(
      List.of(List.of("测试修改1", 1), List.of("测试修改2", 2))
    );
    Assertions.assertTrue(Arrays.stream(executedBatch).allMatch(i -> i == 1));
  }
}

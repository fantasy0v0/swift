package test.parameter;

import com.github.fantasy0v0.swift.Context;
import com.github.fantasy0v0.swift.parameter.ParameterGetter;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.container.SwiftJdbcExtension;

import java.sql.ResultSetMetaData;
import java.time.*;
import java.util.Set;

import static com.github.fantasy0v0.swift.Swift.select;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author fan 2025/9/30
 */
@ExtendWith(SwiftJdbcExtension.class)
public class GetterTest {

  private static final Logger log = LoggerFactory.getLogger(GetterTest.class);

  @TestTemplate
  void testBoolean() {
    Boolean p = false;
    Boolean db = select("select ?", p).fetchOne(row -> row.getBoolean(1));
    assertEquals(p, db);
  }

  @TestTemplate
  void testByte() {
    Byte p = (byte) 2;
    Byte db = select("select ?", p).fetchOne(row -> row.getByte(1));
    assertEquals(p, db);
  }

  @TestTemplate
  void testDouble() {
    Double p = (double) 3;
    Double db = select("select ?", p).fetchOne(row -> row.getDouble(1));
    assertEquals(p, db);
  }

  @TestTemplate
  void testFloat() {
    Float p = (float) 4;
    Float db = select("select ?", p).fetchOne(row -> row.getFloat(1));
    assertEquals(p, db);
  }

  @TestTemplate
  void testInteger() {
    Integer p = 5;
    Integer db = select("select ?", p).fetchOne(row -> row.getInt(1));
    assertEquals(p, db);
  }

  @TestTemplate
  void testLong() {
    Long p = 6L;
    Long db = select("select ?", p).fetchOne(row -> row.getLong(1));
    assertEquals(p, db);
  }

  @TestTemplate
  void testShort() {
    Short p = 7;
    Short db = select("select ?", p).fetchOne(row -> row.getShort(1));
    assertEquals(p, db);
  }

  @TestTemplate
  void testString() {
    String p = "test";
    String db = select("select ?", p).fetchOne(row -> row.getString(1));
    assertEquals(p, db);
  }

  @TestTemplate
  void testObject() {
    Object p = null;
    Object db = select("select ?", p).fetchOne(row -> row.getObject(1));
    assertEquals(p, db);
  }

  @TestTemplate
  void testLocalDateTime() {
    LocalDateTime p = LocalDateTime.now();
    LocalDateTime db = select("select ?", p).fetchOne(row -> row.getLocalDateTime(1));
    Duration duration = Duration.between(p, db);
    assertTrue(Math.abs(duration.getSeconds()) <= 1);

    db = select("select localtimestamp").fetchOne(row -> row.getLocalDateTime(1));
    duration = Duration.between(p, db);
    assertTrue(Math.abs(duration.getSeconds()) <= 1);
  }

  @TestTemplate
  void testLocalTime() {
    LocalTime p = LocalTime.now();
    LocalTime db = select("select ?", p).fetchOne(row -> row.getLocalTime(1));
    assertEquals(p.getHour(), db.getHour());
    assertEquals(p.getMinute(), db.getMinute());
    assertEquals(p.getSecond(), db.getSecond());
  }

  @TestTemplate
  void testLocalDate() {
    LocalDate p = LocalDate.now();
    LocalDate db = select("select ?", p).fetchOne(row -> row.getLocalDate(1));
    assertEquals(p, db);
  }

  @TestTemplate
  void testOffsetDateTime() {
    OffsetDateTime p = OffsetDateTime.now();
    OffsetDateTime db = select("select ?", p).fetchOne(row -> row.getOffsetDateTime(1));
    Duration duration = Duration.between(p, db);
    assertTrue(Math.abs(duration.getSeconds()) <= 1);

    db = select("select current_timestamp").fetchOne(row -> row.getOffsetDateTime(1));
    duration = Duration.between(p, db);
    assertTrue(Math.abs(duration.getSeconds()) <= 1);
  }

  @TestTemplate
  void testCustom(Context context) {
    context.configure(new ParameterGetter<String>() {

      @Override
      public Set<Class<? extends String>> support() {
        return Set.of(String.class);
      }

      @Override
      public String get(ResultSetMetaData metaData, int columnIndex, Object parameter) {
        if (parameter instanceof String value) {
          return value + " by Custom";
        }
        return "Error";
      }
    });
    String value = select("""
    select ?
    """, "test").fetchOne(row -> row.getString(1));
    assertEquals("test by Custom", value);
  }

}

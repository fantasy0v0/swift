package test.vo;

import com.github.fantasy0v0.swift.jdbc.Row;

import java.sql.SQLException;

public record Student(long id, String name, long status) {

  public static Student from(Row row) throws SQLException {
    return new Student(
      row.getLong(1),
      row.getString(2),
      row.getLong(3)
    );
  }

}

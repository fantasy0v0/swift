package com.github.fantasy0v0.swift.jdbc.type;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface TypeSetHandler<T> {

  Class<T> support();

  void doSet(Connection con, PreparedStatement ps, int index, T parameter) throws SQLException;

}

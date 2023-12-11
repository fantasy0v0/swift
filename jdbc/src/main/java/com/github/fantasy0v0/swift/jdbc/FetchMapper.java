package com.github.fantasy0v0.swift.jdbc;

import java.sql.SQLException;

@FunctionalInterface
public interface FetchMapper<R> {

  R apply(Row row) throws SQLException;

}

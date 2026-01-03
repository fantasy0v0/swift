package com.github.fantasy0v0.swift;

import java.sql.SQLException;

public interface FetchMapper<R> {

  R apply(Row row) throws SQLException;

}

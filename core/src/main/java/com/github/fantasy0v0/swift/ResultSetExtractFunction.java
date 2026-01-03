package com.github.fantasy0v0.swift;

import java.sql.SQLException;

public interface ResultSetExtractFunction<P> {

  P apply(int columnIndex) throws SQLException;

}

package com.github.fantasy0v0.swift.jdbc;

import java.sql.ResultSet;

public class Row {

  private final ResultSet resultSet;

  Row(ResultSet resultSet) {
    this.resultSet = resultSet;
  }

}

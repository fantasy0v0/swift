package com.github.fantasy0v0.swift.jdbc;

import java.sql.SQLException;

public interface TransactionRunner {

  void apply() throws SQLException;

}

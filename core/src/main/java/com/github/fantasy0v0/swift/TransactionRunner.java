package com.github.fantasy0v0.swift;

import java.sql.SQLException;

public interface TransactionRunner {

  void apply() throws SQLException;

}

package com.github.fantasy0v0.swift.connection;

import java.sql.SQLException;

/**
 * @author fan 2026/1/16
 */
public interface ManagedSavepoint {

  void release() throws SQLException;

  void rollback() throws SQLException;

}

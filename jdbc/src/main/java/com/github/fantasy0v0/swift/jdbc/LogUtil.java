package com.github.fantasy0v0.swift.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class LogUtil {

  private static final String PackageName = "com.github.fantasy0v0.swift.jdbc";

  LogUtil() {

  }

  static Logger common() {
    return LoggerFactory.getLogger(PackageName + ".common");
  }

  static Logger sql() {
    return LoggerFactory.getLogger(PackageName + ".sql");
  }

  static Logger performance() {
    return LoggerFactory.getLogger(PackageName + ".performance");
  }

}

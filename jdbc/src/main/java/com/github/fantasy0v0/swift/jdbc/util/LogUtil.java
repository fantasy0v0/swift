package com.github.fantasy0v0.swift.jdbc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LogUtil {

  private static final String PackageName = "com.github.fantasy0v0.swift.jdbc";

  LogUtil() {

  }

  public static Logger common() {
    return LoggerFactory.getLogger(PackageName + ".common");
  }

  public static Logger sql() {
    return LoggerFactory.getLogger(PackageName + ".sql");
  }

  public static Logger performance() {
    return LoggerFactory.getLogger(PackageName + ".performance");
  }

}

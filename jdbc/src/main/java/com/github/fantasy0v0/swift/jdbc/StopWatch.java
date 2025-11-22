package com.github.fantasy0v0.swift.jdbc;

import java.math.RoundingMode;
import java.text.NumberFormat;

class StopWatch {

  private final long startTime;

  private long stopTime = -1;

  StopWatch() {
    startTime = System.nanoTime();
  }

  static NumberFormat format = NumberFormat.getInstance();

  static  {
    format.setMaximumFractionDigits(2);
    format.setRoundingMode(RoundingMode.UP);
  }

  private String format(double time, String unit) {
    return format.format(time) + " " + unit;
  }

  private String summary() {
    long elapsed = elapsed();
    if (elapsed > 1_000_000_000) {
      return format(elapsed / 1_000_000_000d, "s");
    } else if (elapsed > 1_000_000) {
      return format(elapsed / 1_000_000d, "ms");
    } else if (elapsed > 1_000) {
      return format(elapsed / 1_000d, "μs");
    } else {
      return elapsed + " ns";
    }
  }

  public long stop() {
    stopTime = System.nanoTime();
    return elapsed();
  }

  public long elapsed() {
    return stopTime == -1 ? System.nanoTime() - startTime : stopTime - startTime;
  }

  @Override
  public String toString() {
    return summary();
  }
}

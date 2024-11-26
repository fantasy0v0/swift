package com.github.fantasy0v0.swift.jdbc;

import java.math.RoundingMode;
import java.text.NumberFormat;

class StopWatch {

  private final long startTime;

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
    long cost = System.nanoTime() - startTime;
    if (cost > 1_000_000_000) {
      return format(cost / 1_000_000_000d, "s");
    } else if(cost > 1_000_000) {
      return format(cost / 1_000_000d, "ms");
    } else if(cost > 1_000) {
      return format(cost / 1_000d, "μs");
    } else {
      return cost + " ns";
    }
  }

  @Override
  public String toString() {
    return summary();
  }
}

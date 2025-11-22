package com.github.fantasy0v0.swift.jdbc;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class StopWatchTest {

  private final Logger log = LoggerFactory.getLogger(StopWatchTest.class);

  @Test
  void summary() throws InterruptedException {
    StopWatch stopWatch = new StopWatch();
    Thread.sleep(10);
    log.debug("cost: {}", stopWatch);
  }
}

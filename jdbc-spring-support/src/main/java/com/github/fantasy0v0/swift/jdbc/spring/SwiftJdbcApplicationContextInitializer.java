package com.github.fantasy0v0.swift.jdbc.spring;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class SwiftJdbcApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    ContextUtil.applicationContext = applicationContext;
  }
}

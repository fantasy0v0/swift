package com.github.fantasy0v0.swift.spring;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class SwiftApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    ContextUtil.applicationContext = applicationContext;
  }
}

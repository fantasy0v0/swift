package com.github.fantasy0v0.swift.jdbc.spring;

import org.springframework.context.ApplicationContext;

final class ContextUtil {

  static ApplicationContext applicationContext;

  static <T> T getBean(Class<T> clazz) {
    return applicationContext.getBean(clazz);
  }

}

package com.github.fantasy0v0.swift.jdbc;

public interface ParameterHandler extends TypeHandler<Object> {

  default Class<Object> supported() {
    return Object.class;
  }

}

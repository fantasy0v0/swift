package com.github.fantasy0v0.swift.core.builder;

public final class Builder {

  private Builder() {

  }

  public static SwiftServerBuilder server() {
    return new SwiftServerBuilder();
  }

}

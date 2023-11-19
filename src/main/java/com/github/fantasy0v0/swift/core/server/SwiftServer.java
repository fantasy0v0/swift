package com.github.fantasy0v0.swift.core.server;

import io.helidon.webserver.WebServer;
import org.graalvm.polyglot.Context;

public final class SwiftServer {

  private final WebServer webServer;

  SwiftServer(WebServer webServer) {
    this.webServer = webServer;
  }

  public static SwiftServerBuilder builder() {
    return new SwiftServerBuilder();
  }

  public SwiftServer start() {
    webServer.start();
    return this;
  }

  public SwiftServer stop() {
    webServer.start();
    return this;
  }

  public boolean isRunning() {
    return webServer.isRunning();
  }

  public int port() {
    return webServer.port();
  }

}

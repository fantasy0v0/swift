package com.github.fantasy0v0.swift.core.server;

import com.github.fantasy0v0.swift.core.server.SwiftServer;
import io.helidon.webserver.WebServer;

public class SwiftServerBuilder {

  private int backlog = 1024;

  private int maxConcurrentRequests = -1;

  private int maxTcpConnections = -1;

  private int port = 0;

  private long maxPayloadSize = -1L;

  SwiftServerBuilder() {

  }

  public void backlog(int backlog) {
    this.backlog = backlog;
  }

  public void maxConcurrentRequests(int maxConcurrentRequests) {
    this.maxConcurrentRequests = maxConcurrentRequests;
  }

  public void maxTcpConnections(int maxTcpConnections) {
    this.maxTcpConnections = maxTcpConnections;
  }

  public void port(int port) {
    this.port = port;
  }

  public void maxPayloadSize(long maxPayloadSize) {
    this.maxPayloadSize = maxPayloadSize;
  }

  public SwiftServer build() {
    WebServer webServer = WebServer.create(builder -> {
      builder
        .backlog(backlog)
        .maxConcurrentRequests(maxConcurrentRequests)
        .maxTcpConnections(maxTcpConnections)
        .port(port)
        .maxPayloadSize(maxPayloadSize);
    });
    return new SwiftServer(webServer);
  }

}
